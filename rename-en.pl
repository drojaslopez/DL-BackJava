# Spanish -> English identifier renames (perl -pi mode)
# Ordered most-specific -> general to avoid substring collisions.

# ---- Application classes / main app ----
s/DesafioHogarApplication/HogarApplication/g;

# ---- Exceptions ----
s/CompraInvalidaException/InvalidPurchaseException/g;
s/RecursoNoEncontradoException/ResourceNotFoundException/g;

# ---- Use cases (specific before generic) ----
s/RegistrarUsuarioUseCase/CreateUserUseCase/g;
s/GestionarUsuarioUseCase/ManageUserUseCase/g;
s/RegistrarCompraUseCase/RegisterPurchaseUseCase/g;
s/GestionarCompraUseCase/ManagePurchaseUseCase/g;
s/GestionarCategoriaUseCase/ManageCategoryUseCase/g;

# ---- Commands / Responses ----
s/CrearUsuarioCommand/CreateUserCommand/g;
s/ActualizarUsuarioCommand/UpdateUserCommand/g;
s/RegistrarCompraCommand/RegisterPurchaseCommand/g;
s/ActualizarCompraCommand/UpdatePurchaseCommand/g;
s/CrearCategoriaCommand/CreateCategoryCommand/g;
s/ActualizarCategoriaCommand/UpdateCategoryCommand/g;
s/UsuarioResponse/UserResponse/g;
s/CompraResponse/PurchaseResponse/g;
s/CategoriaResponse/CategoryResponse/g;
s/ProyeccionResponse/ProjectionResponse/g;
s/ProyeccionMensual/MonthlyProjection/g;
s/LineaGastoPeriodo/ExpenseLine/g;
s/CategoriaTotal/CategoryTotal/g;
s/CuotaResponse/InstallmentResponse/g;

# ---- Controllers ----
s/UsuarioController/UserController/g;
s/CompraController/PurchaseController/g;
s/CategoriaController/CategoryController/g;
s/ReporteController/ReportController/g;

# ---- Value objects ----
s/CompraId/PurchaseId/g;
s/UsuarioId/UserId/g;
s/CuotaId/InstallmentId/g;
s/Moneda/Money/g;

# ---- Repositories (domain) ----
s/UsuarioRepository/UserRepository/g;
s/CompraRepository/PurchaseRepository/g;
s/CategoriaRepository/CategoryRepository/g;

# ---- Enums ----
s/MetodoPagoEnum/PaymentMethod/g;
s/TipoGastoEnum/ExpenseType/g;
s/EtiquetaAmbitoEnum/ExpenseScope/g;
s/EstadoCuotaEnum/InstallmentStatus/g;

# ---- JPA + adapters ----
s/UsuarioJpaRepository/UserJpaRepository/g;
s/UsuarioJpa/UserJpa/g;
s/UsuarioRepositoryAdapter/UserRepositoryAdapter/g;
s/CompraJpaRepository/PurchaseJpaRepository/g;
s/CompraJpa/PurchaseJpa/g;
s/CompraRepositoryAdapter/PurchaseRepositoryAdapter/g;
s/CategoriaJpaRepository/CategoryJpaRepository/g;
s/CategoriaJpa/CategoryJpa/g;
s/CategoriaRepositoryAdapter/CategoryRepositoryAdapter/g;

# ---- Tests ----
s/RegistrarUsuarioUseCaseTest/CreateUserUseCaseTest/g;
s/GestionarUsuarioUseCaseTest/ManageUserUseCaseTest/g;
s/RegistrarCompraUseCaseTest/RegisterPurchaseUseCaseTest/g;
s/GestionarCompraUseCaseTest/ManagePurchaseUseCaseTest/g;
s/GestionarCategoriaUseCaseTest/ManageCategoryUseCaseTest/g;
s/CompraTest/PurchaseTest/g;
s/MonedaTest/MoneyTest/g;
s/UsuarioTest/UserTest/g;

# ==================================================
# Fields / variables / getters (camelCase tokens)
# ==================================================
# montoTotal / getMontoTotal / setMontoTotal
s/MontoTotal/TotalAmount/g;
s/montoTotal/totalAmount/g;
# numeroCuotas / getNumeroCuotas
s/NumeroCuotas/InstallmentCount/g;
s/numeroCuotas/installmentCount/g;
# cuotasGeneradas
s/CuotasGeneradas/Installments/g;
s/cuotasGeneradas/installments/g;
# numeroCuota / getNumeroCuota
s/NumeroCuota/InstallmentNumber/g;
s/numeroCuota/installmentNumber/g;
# periodoVencimiento / getPeriodoVencimiento
s/PeriodoVencimiento/DuePeriod/g;
s/periodoVencimiento/duePeriod/g;
# fechaCompra / getFechaCompra
s/FechaCompra/PurchaseDate/g;
s/fechaCompra/purchaseDate/g;
# usuarioId / getUsuarioId
s/UsuarioId/UserId/g;
s/usuarioId/userId/g;
# etiquetaAmbito / getEtiquetaAmbito
s/EtiquetaAmbito/Scope/g;
s/etiquetaAmbito/scope/g;
# tipoGasto / getTipoGasto
s/TipoGasto/ExpenseType/g;
s/tipoGasto/expenseType/g;
# metodoPago / getMetodoPago
s/MetodoPago/PaymentMethod/g;
s/metodoPago/paymentMethod/g;
# entidadFinanciera / getEntidadFinanciera
s/EntidadFinanciera/FinancialInstitution/g;
s/entidadFinanciera/financialInstitution/g;
# monto / getMonto
s/Monto/Amount/g;
s/monto/amount/g;
# categoria / getCategoria
s/Categoria/Category/g;
s/categoria/category/g;
# period / periodo
s/Periodo/Period/g;
s/periodo/period/g;

# ---- Report response fields ----
s/desglosePorTipoGasto/byExpenseType/g;
s/desglosePorAmbito/byScope/g;
s/porCategoria/byCategory/g;
s/totalGastoMes/monthTotal/g;
s/totalComprometido/committedTotal/g;
s/proyecciones/projections/g;

# ==================================================
# Action method names
# ==================================================
s/ejecutar/execute/g;
s/listar/list/g;
s/obtener/getById/g;
s/actualizar/update/g;
s/eliminar/delete/g;
s/desactivar/deactivate/g;
s/crear/create/g;
s/cambiarNombre/rename/g;
s/marcarPagada/markPaid/g;
s/generarProyeccion/generateProjection/g;
s/lineasDeGastoEn/expenseLinesIn/g;
s/cuotasComprometidasDesde/committedInstallmentsFrom/g;
s/generarDashboard/generateDashboard/g;

# ---- Enum VALUES ----
s/TARJETA_CREDITO/CREDIT_CARD/g;
s/TARJETA_DEBITO/DEBIT_CARD/g;
s/EFECTIVO/CASH/g;
s/TRANSFERENCIA/TRANSFER/g;
s/PENDIENTE/PENDING/g;
s/PAGADA/PAID/g;
s/FIJO/FIXED/g;
s/VARIABLE/VARIABLE/g;
s/HOGAR/HOME/g;
s/SALIDA/OUTING/g;

# ---- Local variables ----
s/\bcompras\b/purchases/g;
s/\bCompra\b/Purchase/g;
s/\bcompra\b/purchase/g;
s/\bUsuario\b/User/g;
s/\busuario\b/user/g;
s/\bCuota\b/Installment/g;
s/\bcuota\b/installment/g;
s/\bCuotas\b/Installments/g;
s/\bcuotas\b/installments/g;
s/\bCategoria\b/Category/g;
s/\bcategoria\b/category/g;
s/\bmonto\b/amount/g;
