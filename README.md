# TradeForge — Design Pattern Lab Project

A clean, beginner-friendly desktop stock trading simulation built using **Java 11**, **Java Swing**, **MVC Architecture**, **JDBC**, and **MySQL**. It demonstrates **10 Gang of Four (GoF) Design Patterns** in an interconnected, authentic trading workflow.

---

## 1. Quick Start — How to Run

### Prerequisites
- **JDK 11 or higher** (Oracle JDK, OpenJDK, or Eclipse Temurin)
- **Apache Maven 3.8+**
- **MySQL Server 8+** (running on port 3306)

---

### Step 1: Database Setup in MySQL Workbench
1. Open **MySQL Workbench** and connect to your local MySQL server.
2. Open the SQL script [database/tradeforge.sql](file:///d:/DP_LAB_FINAL_Project/database/tradeforge.sql) (`File -> Open SQL Script...`).
3. Click the **Execute (Lightning bolt ⚡)** icon to run the script.
4. This creates the `tradeforge_db` schema with:
   - Tables: `users`, `stocks`, `agents`, `portfolio`, `transactions`
   - Demo User: `anshul` / password `1234` (initial balance: ₹50,000)
   - 3 Broker Agents for Chain of Responsibility:
     - **Agent 1** (Rahul Sharma) -> Tier 1: 1–500 stocks
     - **Agent 2** (Priya Patel) -> Tier 2: 501–700 stocks
     - **Agent 3** (Amit Verma) -> Tier 3: >700 stocks
   - 8 Blue-chip stocks: `TCS`, `INFY`, `RELIANCE`, `HDFCBANK`, `ICICIBANK`, `WIPRO`, `ITC`, `SBIN`.

---

### Step 2: Configure Database Credentials (If Different from Default)
TradeForge defaults to username `root` and password `root`. If your MySQL root user has a different password, set it in PowerShell before running:

```powershell
$env:TRADEFORGE_DB_USER="root"
$env:TRADEFORGE_DB_PASSWORD="your_mysql_password"
```

*(Optional custom JDBC URL: `$env:TRADEFORGE_DB_URL="jdbc:mysql://localhost:3306/tradeforge_db?serverTimezone=UTC"`)*

---

### Step 3: Run the Application

#### Option A: One-Click Launch (Batch Script)
Double-click `run.bat` in the project root, or execute in terminal:
```cmd
run.bat
```

#### Option B: Exact PowerShell Commands
Open PowerShell in the project directory (`d:\DP_LAB_FINAL_Project`):
```powershell
# 1. Build the standalone executable JAR
mvn clean package

# 2. Run the application JAR
java -jar target/tradeforge.jar
```

#### Option C: Run Directly via Maven Exec Plugin
```powershell
mvn exec:java
```

#### Option D: Run in VS Code
1. Open the project folder `DP_LAB_FINAL_Project` in VS Code.
2. Install the **Extension Pack for Java** if not already installed.
3. Open [`com/tradeforge/Main.java`](file:///d:/DP_LAB_FINAL_Project/src/main/java/com/tradeforge/Main.java).
4. Click the **Run** button (or press `F5`).

---

## 2. Default Login Credentials
- **Username:** `anshul`
- **Password:** `1234`

---

## 3. The 10 Design Patterns Mapping Table

| # | Design Pattern | Type | Primary Classes / Interfaces | Responsibility in TradeForge |
|---|----------------|------|------------------------------|-------------------------------|
| 1 | **Singleton** | Creational | `DatabaseConnection` | Manages a shared, thread-safe JDBC connection with automatic reconnection and environment variable support. |
| 2 | **Factory** | Creational | `TransactionFactory`, `Transaction`, `BuyTransaction`, `SellTransaction` | Instantiates `BUY` or `SELL` transaction calculation products (calculates 2% brokerage charges and net totals). |
| 3 | **Proxy** | Structural | `TradingServiceProxy`, `TradingService`, `RealTradingService` | Enforces protection and access control: verifies user is authenticated and session user ID matches request before delegating to the real service. |
| 4 | **Chain of Responsibility** | Behavioral | **Agent Chain:** `AgentAssignmentHandler`, `Tier1AgentHandler`, `Tier2AgentHandler`, `Tier3AgentHandler`<br>**Validation Chain:** `ValidationHandler`, `UserValidationHandler`, `StockValidationHandler`, `BalanceValidationHandler`, `QuantityValidationHandler` | 1. Assigns broker agent based on quantity thresholds: 1–500 (Agent 1), 501–700 (Agent 2), >700 (Agent 3).<br>2. Validates user status, stock existence, balance, and inventory in sequence. |
| 5 | **Observer** | Behavioral | `StockTradingSubject`, `TradingObserver`, `Observer`, `UserNotificationObserver` | Publishes event messages upon successful trade execution to notify the GUI notification banner in real time. |
| 6 | **Builder** | Creational | `TradeBuilder` | Provides fluent, step-by-step construction of `TradingRequest` objects with strict field validation (`userId`, `stockId`, `type`, `quantity`). |
| 7 | **Mediator** | Behavioral | `TradingMediator` | Central coordinator that decouples the Swing Controllers from DAOs, Proxy, CoR handlers, Template workflows, and Observers. |
| 8 | **Template Method** | Behavioral | `TradingTemplate`, `BuyTradingWorkflow`, `SellTradingWorkflow`, `TradeResult` | Invariant 5-step execution algorithm (`validate -> checkAvailability -> executeOperation -> saveTransaction -> notifyObservers`) with customizable BUY and SELL hooks. |
| 9 | **Memento** | Behavioral | `TradeMemento`, `TradeCaretaker` | Captures, saves, and restores UI draft form states (stock, type, quantity) with "Save Draft" and "Undo/Restore" without touching committed DB transactions. |
| 10 | **Prototype** | Creational | `TradePrototype`, `TradingRequest` | Deep-clones trade requests with "Duplicate Trade" to populate an independent, editable draft without immediate DB execution. |

---

## 4. Architecture & MVC Pattern

TradeForge strictly separates concerns into **Model-View-Controller (MVC)** and Data Access Objects (DAO):

```
                        [ USER (GUI Interaction) ]
                                    |
                                    v
                             +-------------+
                             |    VIEW     |
                             | LoginFrame  |
                             |  Dashboard  |
                             +------+------+
                                    |  (User Events)
                                    v
                             +-------------+
                             | CONTROLLER  |
                             | LoginCtrl   |
                             | DashboardCtl|
                             +------+------+
                                    |  (Calls high-level actions)
                                    v
                     +-------------------------------+
                     |      TRADING MEDIATOR         |
                     |  (Coordinates Subsystems)     |
                     +--+--------+---------+-------+-+
                        |        |         |       |
       +----------------+   +----+----+    |       +----------------+
       v                    v         v    v                        v
  [ Builder ]            [ Proxy ]  [ CoR Agent Chain ]        [ Observer ]
  TradeBuilder              |       Tier1 / Tier2 / Tier3     Subject / Observer
                            v
                   [ RealTradingService ]
                            |
                            v
                 [ Template Method Workflows ]
                 BuyTradingWorkflow / SellTradingWorkflow
                            |
       +--------------------+--------------------+
       v                                         v
   [ DAOs ]                                 [ Factory ]
UserDAO, StockDAO,                      TransactionFactory
PortfolioDAO, TransactionDAO                     |
       |                                         v
       v                                [ BUY / SELL Math ]
[ DatabaseConnection (Singleton) ]
       |
       v
  [ MySQL DB ]
```

---

## 5. How to Demonstrate Each Pattern in GUI & Viva

1. **Singleton Pattern**:
   - Class: `com.tradeforge.singleton.DatabaseConnection`.
   - Viva Explanation: Ensures only a single shared database connection manager exists throughout application lifecycle, preventing connection leaks.
2. **Factory Pattern**:
   - Change Order Type between **BUY** and **SELL** in the Trading Desk.
   - Observe the live fee breakdown: BUY adds 2% fee to total; SELL deducts 2% fee from proceeds. Handled by `TransactionFactory`.
3. **Chain of Responsibility Pattern (Agent Assignment)**:
   - In Trading Desk, change the quantity spinner:
     - Enter **100** -> Assigned Agent dynamically shows: **Agent 1 - Rahul Sharma (Tier 1: 1–500 stocks)** (Blue Badge).
     - Enter **600** -> Assigned Agent dynamically changes to: **Agent 2 - Priya Patel (Tier 2: 501–700 stocks)** (Yellow Badge).
     - Enter **800** -> Assigned Agent dynamically changes to: **Agent 3 - Amit Verma (Tier 3: >700 stocks)** (Red Badge).
4. **Builder Pattern**:
   - Click "Execute Order". The GUI constructs the request using `new TradeBuilder().setUserId(...).setStockId(...).setType(...).setQuantity(...).build()`.
5. **Proxy Pattern**:
   - `TradingServiceProxy` inspects the request before any DB write. If `authenticatedUserId <= 0` or doesn't match the order's user ID, an access violation is thrown.
6. **Template Method Pattern**:
   - `BuyTradingWorkflow` and `SellTradingWorkflow` extend `TradingTemplate`, executing the identical 5-stage algorithm while customizing the balance deduction vs. portfolio addition logic.
7. **Observer Pattern**:
   - After a trade completes, observe the purple **[OBSERVER NOTIFICATION]** banner at the top of Dashboard updating in real-time.
8. **Memento Pattern**:
   - Select stock `INFY`, type `BUY`, quantity `50`. Click **"Save Draft (Memento)"**.
   - Change quantity to `100` and type to `SELL`.
   - Click **"Undo / Restore (Memento)"** -> Watch the form instantly revert back to `INFY`, `BUY`, and `50`.
9. **Prototype Pattern**:
   - Select a row in the Market Stocks or Transaction History tab and click **"Duplicate Selected Trade (Prototype)"**.
   - Values are cloned into the editable Trading Desk without committing any database transaction yet.
10. **Mediator Pattern**:
    - `TradingMediator` unifies and orchestrates all 9 other patterns, so `DashboardController` only needs a single point of interaction.

---

## 6. Running Automated Unit Tests
To run the automated test suite verifying all 10 patterns:
```powershell
mvn test
```
Result: 9 automated unit tests covering all 10 design patterns, agent thresholds, fee calculations, security proxy checks, and memento/prototype isolation pass with **0 failures**.
