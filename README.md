# BadgerDB

A lightweight Java-based database management system with natural language query capabilities.

## Overview

BadgerDB is a simple but powerful database engine written in Java that implements:
- Buffered storage management with LRU page caching
- Table management with a catalog system
- SQL-like query operations (SELECT, INSERT, CREATE, etc.)
- Natural language query processing (using machine learning)

This project demonstrates core database concepts including:
- Data page management and allocation
- Buffering strategies
- Catalog management
- Basic query parsing and execution

## Components

### Storage Layer
- **StorageEngine.java**: Handles raw data storage and retrieval, managing database files and pages
- **LRU.java**: Implements a Least Recently Used cache for efficient memory management

### Data Management Layer
- **CatalogManager.java**: Manages database schemas, tables, and their metadata
- **tableItem.java**: Data structure for representing table information

### Query Processing Layer
- **QueryParser.java**: Parses and executes SQL-like queries
- **Natural Language Processing**: Python-based ML model for translating natural language to database commands

## How It Works

BadgerDB stores data in pages of fixed size (4KB by default). The database consists of:
1. **Admin Pages**: Store metadata about the database structure
2. **Data Pages**: Store actual table records using a slotted page architecture

The query interface allows users to:
- Create tables with specified columns and data types
- Insert data into tables
- Query data using SELECT statements
- List available tables
- Show table structures

## Commands Supported

```
-- Create a new table
CREATE TABLE <tablename> <dtype> <colname>, <dtype> <colname>, ...

-- Insert data
INSERT INTO <tablename> VALUES <val1>, <val2>, ...

-- Query data
SELECT * FROM <tablename>
SELECT <colname1>,<colname2>,... FROM <tablename>

-- List all tables
LIST

-- Show table schema
SHOW <tablename>
```

## Natural Language Query Support

BadgerDB includes a Python-based machine learning component (using the Microsoft Phi-2 model with LoRA fine-tuning) that translates natural language queries into database commands. This allows users to interact with the database using plain English.

Examples:
- "Show me all employees" → `SELECT * FROM employees`
- "Create a new table for storing student information" → `CREATE TABLE students INT id, STRING name, INT age, STRING major`
- "Add John who is 25 years old to the users table" → `INSERT INTO users VALUES John, 25`

## Usage

1. Compile the Java files:
```
javac *.java
```

2. Run the database:
```
java QueryParser
```

3. Enter commands at the prompt:
```
>> CREATE TABLE employees INT id, STRING name, STRING department, INT salary, BOOLEAN active
>> INSERT INTO employees VALUES 1, John Doe, Engineering, 85000, true
>> SELECT * FROM employees
```

## Technical Details

- **Page Structure**: Each page contains a header with metadata and slotted storage for records
- **Record Format**: Records contain field data according to the table schema
- **Buffering**: The LRU cache minimizes disk I/O by keeping frequently accessed pages in memory
- **Catalog**: Table metadata is stored in admin pages, including schema information

## Future Improvements

- Transaction support
- Indexing for faster queries
- Extended query support (WHERE clauses, JOIN operations)
- Enhanced natural language understanding

## License

null

## Contributing

:o
