#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Structure Analyzer - Tool to analyze and compare project structures
based on YAML configuration templates.
"""

import os
import re
import sys
import yaml
import argparse
from pathlib import Path
from typing import Dict, List, Any, Optional, Set
from dataclasses import dataclass, field
from enum import Enum

# Configure encoding for Windows
if sys.platform == 'win32':
    import codecs
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.buffer, 'strict')
    sys.stderr = codecs.getwriter('utf-8')(sys.stderr.buffer, 'strict')


class Severity(Enum):
    INFO = "INFO"
    WARNING = "WARNING"
    ERROR = "ERROR"


@dataclass
class ValidationIssue:
    """Represents an issue found during validation"""
    rule: str
    description: str
    severity: Severity
    location: Optional[str] = None
    suggestion: Optional[str] = None


@dataclass
class StructureComparison:
    """Result of comparing the expected and the actual structure"""
    missing_directories: List[str] = field(default_factory=list)
    missing_files: List[str] = field(default_factory=list)
    missing_packages: List[str] = field(default_factory=list)
    extra_directories: List[str] = field(default_factory=list)
    structure_matches: bool = True
    issues: List[ValidationIssue] = field(default_factory=list)


class ProjectTemplate:
    """Represents a project template loaded from YAML"""

    def __init__(self, template_path: str):
        self.template_path = template_path
        self.config = self._load_template()

    def _load_template(self) -> Dict[str, Any]:
        """Load the YAML template file"""
        try:
            with open(self.template_path, 'r', encoding='utf-8') as f:
                return yaml.safe_load(f)
        except FileNotFoundError:
            raise FileNotFoundError(f"Template not found: {self.template_path}")
        except yaml.YAMLError as e:
            raise ValueError(f"Error parsing YAML: {e}")

    @property
    def base_package(self) -> str:
        """Get the configured base package"""
        return self.config.get('variables', [{}])[0].get('default', 'myproject')

    @property
    def project_name(self) -> str:
        """Get the project name"""
        return self.config.get('variables', [{}])[1].get('default', 'myproject')

    @property
    def required_directories(self) -> List[str]:
        """Get the list of required directories"""
        dirs = []
        for dir_config in self.config.get('directory_structure', {}).get('root', []):
            dirs.append(dir_config['path'])
        return dirs

    @property
    def required_files(self) -> List[str]:
        """Get the list of required files"""
        files = []
        for file_config in self.config.get('required_files', []):
            files.append(file_config['path'])
        return files

    @property
    def package_structure(self) -> Dict[str, Any]:
        """Get the expected package structure"""
        return self.config.get('package_structure', {})

    @property
    def validation_rules(self) -> List[Dict[str, Any]]:
        """Get the validation rules"""
        return self.config.get('validation_rules', [])


class ProjectAnalyzer:
    """Analyzes the structure of an existing project"""

    def __init__(self, project_path: str):
        self.project_path = Path(project_path)
        if not self.project_path.exists():
            raise FileNotFoundError(f"Project not found: {project_path}")
        self.detected_base_package = self._detect_base_package()

    def _detect_base_package(self) -> str:
        """Detect the base package from Java package declarations.

        Parses the `package` statement of every Java file under
        src/main/java and returns the two-level root package
        (e.g. drl.desafio, com.mycompany).
        """
        java_path = self.project_path / 'src' / 'main' / 'java'
        if not java_path.exists():
            return "myproject"

        packages: List[List[str]] = []
        package_pattern = re.compile(r'^\s*package\s+([\w.]+)\s*;', re.MULTILINE)

        for root, _, files in os.walk(java_path):
            for file in files:
                if not file.endswith('.java'):
                    continue
                path = Path(root) / file
                try:
                    content = path.read_text(encoding='utf-8', errors='ignore')
                except OSError:
                    continue
                match = package_pattern.search(content)
                if match:
                    packages.append(match.group(1).split('.'))

        if not packages:
            return "myproject"

        # Longest common prefix across all package declarations
        common = packages[0][:]
        for pkg in packages[1:]:
            i = 0
            while i < len(common) and i < len(pkg) and common[i] == pkg[i]:
                i += 1
            common = common[:i]

        if not common:
            return "myproject"

        # Base package = the two-level root (groupId.company / company.module)
        return '.'.join(common[:2])

    def get_directory_structure(self) -> Dict[str, Any]:
        """Analyze the project directory structure"""
        structure = {
            'directories': [],
            'files': [],
            'packages': {}
        }

        for root, dirs, files in os.walk(self.project_path):
            # Skip build directories but keep important hidden ones
            dirs[:] = [d for d in dirs if d != 'target']

            rel_path = Path(root).relative_to(self.project_path)
            structure['directories'].append(str(rel_path))

            for file in files:
                file_path = rel_path / file
                structure['files'].append(str(file_path))

        return structure

    def detect_package_structure(self) -> Set[str]:
        """Detect the full dotted package names present in the project"""
        packages: Set[str] = set()
        java_path = self.project_path / 'src' / 'main' / 'java'

        if java_path.exists():
            for root, _, files in os.walk(java_path):
                for file in files:
                    if not file.endswith('.java'):
                        continue
                    rel_path = Path(root).relative_to(java_path)
                    if str(rel_path) == '.':
                        continue
                    packages.add('.'.join(rel_path.parts))

        return packages

    def check_maven_config(self) -> Dict[str, Any]:
        """Check the Maven configuration if present"""
        pom_path = self.project_path / 'pom.xml'
        if not pom_path.exists():
            return {'exists': False}

        try:
            with open(pom_path, 'r', encoding='utf-8') as f:
                content = f.read()
                return {
                    'exists': True,
                    'has_junit': 'junit-jupiter' in content,
                    'has_mockito': 'mockito' in content,
                    'has_assertj': 'assertj' in content,
                    'has_jacoco': 'jacoco' in content,
                    'java_version': self._extract_java_version(content)
                }
        except Exception as e:
            return {'exists': True, 'error': str(e)}

    def _extract_java_version(self, pom_content: str) -> Optional[str]:
        """Extract the Java version from the pom.xml content"""
        match = re.search(r'maven\.compiler\.source.*?(\d+)', pom_content)
        if match:
            return match.group(1)
        return None


class StructureComparator:
    """Compares project structures against templates"""

    def __init__(self, template: ProjectTemplate, analyzer: ProjectAnalyzer):
        self.template = template
        self.analyzer = analyzer

    def compare(self) -> StructureComparison:
        """Perform the full comparison"""
        comparison = StructureComparison()

        current_structure = self.analyzer.get_directory_structure()
        current_packages = self.analyzer.detect_package_structure()

        self._check_directories(comparison, current_structure)
        self._check_files(comparison, current_structure)
        self._check_packages(comparison, current_packages)
        self._validate_rules(comparison)

        return comparison

    def _check_directories(self, comparison: StructureComparison,
                           current_structure: Dict[str, Any]):
        """Check required directories"""
        required_dirs = self.template.required_directories
        detected_package = self.analyzer.detected_base_package

        for req_dir in required_dirs:
            req_dir = req_dir.replace('{base_package}', detected_package.replace('.', '/'))
            req_dir = req_dir.replace('{project_name}', self.template.project_name)

            req_dir_normalized = str(req_dir).replace('\\', '/')
            found = False

            for current_dir in current_structure['directories']:
                current_dir_normalized = str(current_dir).replace('\\', '/')
                if req_dir_normalized == current_dir_normalized or req_dir_normalized in current_dir_normalized:
                    found = True
                    break

            if not found:
                comparison.missing_directories.append(req_dir)
                comparison.structure_matches = False

    def _check_files(self, comparison: StructureComparison,
                     current_structure: Dict[str, Any]):
        """Check required files"""
        required_files = self.template.required_files

        for req_file in required_files:
            if not any(req_file in f for f in current_structure['files']):
                comparison.missing_files.append(req_file)
                comparison.structure_matches = False

    def _check_packages(self, comparison: StructureComparison,
                        current_packages: Set[str]):
        """Check the package structure against the expected layers"""
        base_package = self.analyzer.detected_base_package
        expected_layers = self.template.package_structure.get('layers', [])

        for layer in expected_layers:
            layer_pkg = f"{base_package}.{layer['name']}"
            if layer_pkg not in current_packages:
                comparison.missing_packages.append(layer_pkg)
                comparison.structure_matches = False

            for subpackage in layer.get('subpackages', []):
                subpackage_pkg = f"{layer_pkg}.{subpackage['name']}"
                if subpackage_pkg not in current_packages:
                    comparison.missing_packages.append(subpackage_pkg)
                    comparison.structure_matches = False

    def _validate_rules(self, comparison: StructureComparison):
        """Validate business rules"""
        maven_config = self.analyzer.check_maven_config()

        for rule in self.template.validation_rules:
            rule_name = rule['rule']
            description = rule['description']
            severity = Severity[rule['severity'].upper()]

            issue = ValidationIssue(
                rule=rule_name,
                description=description,
                severity=severity
            )

            if rule_name == "test_coverage":
                if not maven_config.get('has_jacoco'):
                    issue.suggestion = "Add the JaCoCo plugin to the pom.xml"
                    comparison.issues.append(issue)

            elif rule_name == "domain_layer_isolation":
                packages = self.analyzer.detect_package_structure()
                domain_pkg = f"{self.analyzer.detected_base_package}.domain"
                if not any(p == domain_pkg or p.startswith(domain_pkg + '.') for p in packages):
                    issue.suggestion = "Create a separate domain package"
                    comparison.issues.append(issue)

            elif rule_name == "interface_injection":
                if maven_config.get('exists'):
                    issue.suggestion = "Review constructor injection for external dependencies"
                    comparison.issues.append(issue)


class ProposalGenerator:
    """Generates correction proposals"""

    def __init__(self, comparison: StructureComparison, template: ProjectTemplate):
        self.comparison = comparison
        self.template = template

    def generate_proposals(self) -> List[str]:
        """Generate the list of correction proposals"""
        proposals = []

        if self.comparison.missing_directories:
            proposals.append(self._generate_directory_proposals())

        if self.comparison.missing_files:
            proposals.append(self._generate_file_proposals())

        if self.comparison.missing_packages:
            proposals.append(self._generate_package_proposals())

        if self.comparison.issues:
            proposals.append(self._generate_issue_proposals())

        return proposals

    def _generate_directory_proposals(self) -> str:
        """Generate proposals for missing directories"""
        proposal = "## Missing Directories\n\n"
        proposal += "Create the following directories:\n\n"

        for directory in self.comparison.missing_directories:
            proposal += f"- `{directory}`\n"

        proposal += "\nCommands to create:\n```bash\n"
        for directory in self.comparison.missing_directories:
            proposal += f"mkdir -p \"{directory}\"\n"
        proposal += "```\n"

        return proposal

    def _generate_file_proposals(self) -> str:
        """Generate proposals for missing files"""
        proposal = "## Missing Files\n\n"
        proposal += "Create the following files:\n\n"

        for file in self.comparison.missing_files:
            proposal += f"- `{file}`\n"

        return proposal

    def _generate_package_proposals(self) -> str:
        """Generate proposals for missing Java packages"""
        proposal = "## Missing Packages\n\n"
        proposal += "Create the following Java packages:\n\n"

        for package in self.comparison.missing_packages:
            proposal += f"- `{package}`\n"

        return proposal

    def _generate_issue_proposals(self) -> str:
        """Generate proposals for validation issues"""
        proposal = "## Validation Issues\n\n"

        for issue in self.comparison.issues:
            icon = "[ERROR]" if issue.severity == Severity.ERROR else "[WARNING]"
            proposal += f"{icon} **{issue.rule}**: {issue.description}\n"
            if issue.suggestion:
                proposal += f"   [SUGGESTION] {issue.suggestion}\n"
            proposal += "\n"

        return proposal


def main():
    """Main function of the script"""
    parser = argparse.ArgumentParser(
        description='Analyzes and compares project structures'
    )
    parser.add_argument(
        'template',
        help='Path to the YAML template file'
    )
    parser.add_argument(
        'project',
        help='Path to the project to analyze'
    )
    parser.add_argument(
        '--interactive',
        action='store_true',
        help='Interactive mode to approve changes'
    )
    parser.add_argument(
        '--output',
        help='Output file for the report'
    )

    args = parser.parse_args()

    try:
        # Load template
        print(f"[+] Loading template: {args.template}")
        template = ProjectTemplate(args.template)

        # Analyze project
        print(f"[+] Analyzing project: {args.project}")
        analyzer = ProjectAnalyzer(args.project)

        # Compare structures
        print("[+] Comparing structures...")
        comparator = StructureComparator(template, analyzer)
        comparison = comparator.compare()

        # Generate proposals
        print("[+] Generating proposals...")
        generator = ProposalGenerator(comparison, template)
        proposals = generator.generate_proposals()

        # Show results
        print("\n" + "=" * 60)
        print("ANALYSIS RESULT")
        print("=" * 60)

        if comparison.structure_matches:
            print("[OK] The project structure matches the template")
        else:
            print("[ERROR] The project structure does NOT match the template")

        print(f"\n[*] Missing directories: {len(comparison.missing_directories)}")
        print(f"[*] Missing files: {len(comparison.missing_files)}")
        print(f"[*] Missing packages: {len(comparison.missing_packages)}")
        print(f"[*] Validation issues: {len(comparison.issues)}")

        if proposals:
            print("\n" + "=" * 60)
            print("CORRECTION PROPOSALS")
            print("=" * 60)
            for proposal in proposals:
                print(proposal)

        # Save report if requested
        if args.output:
            with open(args.output, 'w', encoding='utf-8') as f:
                f.write("STRUCTURE ANALYSIS REPORT\n")
                f.write("=" * 60 + "\n\n")
                f.write(f"Template: {args.template}\n")
                f.write(f"Project: {args.project}\n\n")

                if comparison.structure_matches:
                    f.write("[OK] VALID STRUCTURE\n")
                else:
                    f.write("[ERROR] INVALID STRUCTURE\n")

                f.write(f"\nMissing directories: {len(comparison.missing_directories)}\n")
                f.write(f"Missing files: {len(comparison.missing_files)}\n")
                f.write(f"Missing packages: {len(comparison.missing_packages)}\n")
                f.write(f"Issues: {len(comparison.issues)}\n\n")

                for proposal in proposals:
                    f.write(proposal + "\n")

            print(f"\n[*] Report saved to: {args.output}")

        # Interactive mode
        if args.interactive and not comparison.structure_matches:
            print("\n" + "=" * 60)
            print("INTERACTIVE MODE")
            print("=" * 60)
            print("Do you want to apply the proposed corrections? (y/n)")

            # Interactive logic to apply changes automatically
            # could be implemented here

    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
