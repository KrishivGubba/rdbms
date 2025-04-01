select_examples = {
    "Show me all the records from the students table": "select * from students",
    "Get all data from the employees table": "select * from employees",
    "Retrieve everything from the products table": "select * from products",
    "Pull up all records from the orders table": "select * from orders",
    "Show me the complete customers table": "select * from customers",
    "I need to see all entries in the inventory table": "select * from inventory",
    "Display all rows from the transactions table": "select * from transactions",
    "Show me all data in the users table": "select * from users",
    "Get the entire suppliers table": "select * from suppliers",
    "Pull all records from the appointments table": "select * from appointments",
    
    "Show me the name and age columns from the students table": "select name,age from students",
    "Get the name and salary columns from the employees table": "select name,salary from employees",
    "Show name and price columns from the products table": "select name,price from products",
    "I need the id and date columns from the orders table": "select id,date from orders",
    "Display name and email columns from the customers table": "select name,email from customers",
    "Show name and quantity columns from the inventory table": "select name,quantity from inventory",
    "Get amount and date columns from the transactions table": "select amount,date from transactions",
    "Retrieve username and role columns from the users table": "select username,role from users",
    "Show name and contact columns from the suppliers table": "select name,contact from suppliers",
    "Get time and patient columns from the appointments table": "select time,patient from appointments",
    
    "Show me name, age, and grade columns from the students table": "select name,age,grade from students",
    "Get id, name, and department columns from the employees table": "select id,name,department from employees",
    "I need name, price, and category columns from the products table": "select name,price,category from products",
    "Display id, customer_id, and total columns from the orders table": "select id,customer_id,total from orders",
    "Show name, email, and address columns from the customers table": "select name,email,address from customers",
    "Get id, name, and stock columns from the inventory table": "select id,name,stock from inventory",
    "Show id, amount, and status columns from the transactions table": "select id,amount,status from transactions",
    "Retrieve id, username, and login_date columns from the users table": "select id,username,login_date from users",
    "Display name, product, and price columns from the suppliers table": "select name,product,price from suppliers",
    "Get date, time, and duration columns from the appointments table": "select date,time,duration from appointments",
    
    "Can I see all information from the students table?": "select * from students",
    "Please show everything in the employees table": "select * from employees",
    "I'd like to view all columns from the products table": "select * from products",
    "Could you show me the full orders table?": "select * from orders",
    "Need to check all records from the customers table": "select * from customers",
    
    "Just show me name and age columns from the students table": "select name,age from students",
    "Only need to see id and department columns from the employees table": "select id,department from employees",
    "Get me name and stock columns from the products table": "select name,stock from products",
    "Display date and amount columns from the orders table": "select date,amount from orders",
    "Just the name and phone columns from the customers table please": "select name,phone from customers",
    
    "What are all the name values from the students table?": "select name from students",
    "Give me all salary values from the employees table": "select salary from employees",
    "Show all category values from the products table": "select category from products",
    "I just need all the date values from the orders table": "select date from orders",
    "List all email values from the customers table": "select email from customers",
    
    "Find name and grade columns for all entries in the students table": "select name,grade from students",
    "Get department and hire_date columns for each row in the employees table": "select department,hire_date from employees",
    "Show price and description columns for all items in the products table": "select price,description from products",
    "Pull up customer_id and total columns for each entry in the orders table": "select customer_id,total from orders",
    "Display name and city columns for all records in the customers table": "select name,city from customers"
}


insert_examples = {
    "Add a new row in the students table, for John, age 15, is passing": "insert into students values John, 15, true",
    "Insert a record in the employees table, for Sarah Smith, Engineering department, salary 75000, hire date 2023-05-10": "insert into employees values Sarah Smith, Engineering, 75000, 2023-05-10",
    "Create a new entry in the products table, for Laptop, price 999.99, category Electronics, stock 50": "insert into products values Laptop, 999.99, Electronics, 50",
    "Add to the orders table, order ID 1001, customer ID 5, total 156.78, date 2025-03-01": "insert into orders values 1001, 5, 156.78, 2025-03-01",
    "Insert in the customers table, for Alex Johnson, email alex@example.com, phone 555-1234, city New York": "insert into customers values Alex Johnson, alex@example.com, 555-1234, New York",
    
    "Add a new row in the inventory table, for Printer Paper, quantity 500, category Office Supplies, reorder level 100": "insert into inventory values Printer Paper, 500, Office Supplies, 100",
    "Insert into the transactions table, transaction ID 8765, amount 250.00, date 2025-03-15, status completed": "insert into transactions values 8765, 250.00, 2025-03-15, completed",
    "Create a new user in the users table, username mjones, role admin, last login 2025-03-20": "insert into users values mjones, admin, 2025-03-20",
    "Add a supplier to the suppliers table, name Acme Inc, contact 555-9876, product Electronics, price tier premium": "insert into suppliers values Acme Inc, 555-9876, Electronics, premium",
    "Insert an appointment in the appointments table, date 2025-04-01, time 14:30, patient Maria Garcia, duration 60": "insert into appointments values 2025-04-01, 14:30, Maria Garcia, 60",
    
    "Add a new student in the students table, for Emma, age 16, is passing": "insert into students values Emma, 16, true",
    "Insert an employee in the employees table, for Michael Brown, Marketing department, salary 65000, hire date 2024-10-15": "insert into employees values Michael Brown, Marketing, 65000, 2024-10-15",
    "Create a product in the products table, for Headphones, price 129.99, category Audio, stock 75": "insert into products values Headphones, 129.99, Audio, 75",
    "Add to orders table, order ID 1002, customer ID 8, total 89.50, date 2025-03-05": "insert into orders values 1002, 8, 89.50, 2025-03-05",
    "Insert a customer in the customers table, for Taylor Wilson, email taylor@example.com, phone 555-5678, city Chicago": "insert into customers values Taylor Wilson, taylor@example.com, 555-5678, Chicago",
    
    "Add inventory item in the inventory table, for Notebooks, quantity 200, category Stationery, reorder level 50": "insert into inventory values Notebooks, 200, Stationery, 50",
    "Insert a transaction in the transactions table, transaction ID 8766, amount 75.25, date 2025-03-16, status pending": "insert into transactions values 8766, 75.25, 2025-03-16, pending",
    "Create a user in the users table, username asmith, role user, last login 2025-03-18": "insert into users values asmith, user, 2025-03-18",
    "Add to suppliers table, name TechParts Ltd, contact 555-4321, product Hardware, price tier standard": "insert into suppliers values TechParts Ltd, 555-4321, Hardware, standard",
    "Insert in the appointments table, date 2025-04-02, time 10:15, patient James Wilson, duration 30": "insert into appointments values 2025-04-02, 10:15, James Wilson, 30",
    
    "Add a new row in students table, for David, age 14, is failing": "insert into students values David, 14, false",
    "Insert in employees table, for Jennifer Lee, HR department, salary 70000, hire date 2024-08-20": "insert into employees values Jennifer Lee, HR, 70000, 2024-08-20",
    "Create an entry in products table, for Monitor, price 349.99, category Electronics, stock 25": "insert into products values Monitor, 349.99, Electronics, 25",
    "Add to the orders table, order ID 1003, customer ID 12, total 215.30, date 2025-03-10": "insert into orders values 1003, 12, 215.30, 2025-03-10",
    "Insert a row in customers table, for Sam Rodriguez, email sam@example.com, phone 555-8765, city Miami": "insert into customers values Sam Rodriguez, sam@example.com, 555-8765, Miami",
    
    "Add inventory item in inventory table, for Pens, quantity 1000, category Stationery, reorder level 200": "insert into inventory values Pens, 1000, Stationery, 200",
    "Insert in transactions table, transaction ID 8767, amount 500.00, date 2025-03-17, status completed": "insert into transactions values 8767, 500.00, 2025-03-17, completed",
    "Create a new user entry in users table, username ldavis, role editor, last login 2025-03-19": "insert into users values ldavis, editor, 2025-03-19",
    "Add supplier in suppliers table, name FreshFoods, contact 555-3456, product Groceries, price tier economy": "insert into suppliers values FreshFoods, 555-3456, Groceries, economy",
    "Insert appointment in appointments table, date 2025-04-03, time 15:45, patient Sofia Chen, duration 45": "insert into appointments values 2025-04-03, 15:45, Sofia Chen, 45",
    
    "Add a student record in the students table, for Olivia, age 17, is passing": "insert into students values Olivia, 17, true",
    "Insert employee in the employees table, for Robert Kim, Finance department, salary 85000, hire date 2023-11-05": "insert into employees values Robert Kim, Finance, 85000, 2023-11-05",
    "Create product entry in the products table, for Keyboard, price 59.99, category Electronics, stock 100": "insert into products values Keyboard, 59.99, Electronics, 100",
    "Add order in the orders table, order ID 1004, customer ID 3, total 45.99, date 2025-03-12": "insert into orders values 1004, 3, 45.99, 2025-03-12",
    "Insert customer in the customers table, for Jordan Patel, email jordan@example.com, phone 555-2345, city Boston": "insert into customers values Jordan Patel, jordan@example.com, 555-2345, Boston",
    
    "Add new inventory in the inventory table, for Staplers, quantity 50, category Office Supplies, reorder level 15": "insert into inventory values Staplers, 50, Office Supplies, 15",
    "Insert transaction in the transactions table, transaction ID 8768, amount 125.50, date 2025-03-18, status completed": "insert into transactions values 8768, 125.50, 2025-03-18, completed",
    "Create user in the users table, username jthomas, role user, last login 2025-03-21": "insert into users values jthomas, user, 2025-03-21",
    "Add a supplier entry in the suppliers table, name BuildRight, contact 555-7890, product Construction, price tier premium": "insert into suppliers values BuildRight, 555-7890, Construction, premium",
    "Insert an appointment entry in the appointments table, date 2025-04-04, time 09:00, patient Noah Martinez, duration 60": "insert into appointments values 2025-04-04, 09:00, Noah Martinez, 60",
    
    "Add student in the students table, for William, age 13, is passing": "insert into students values William, 13, true",
    "Insert a row for employees table, for Lisa Wong, Sales department, salary 72000, hire date 2024-02-15": "insert into employees values Lisa Wong, Sales, 72000, 2024-02-15",
    "Create entry for products table, for Mouse, price 29.99, category Electronics, stock 150": "insert into products values Mouse, 29.99, Electronics, 150",
    "Add a record to orders table, order ID 1005, customer ID 7, total 199.95, date 2025-03-14": "insert into orders values 1005, 7, 199.95, 2025-03-14",
    "Insert data in customers table, for Chris Evans, email chris@example.com, phone 555-6789, city Seattle": "insert into customers values Chris Evans, chris@example.com, 555-6789, Seattle",
    
    "Add entry to inventory table, for Folders, quantity 300, category Office Supplies, reorder level 75": "insert into inventory values Folders, 300, Office Supplies, 75",
    "Insert record for transactions table, transaction ID 8769, amount 50.00, date 2025-03-19, status pending": "insert into transactions values 8769, 50.00, 2025-03-19, pending",
    "Create account in users table, username mgreen, role admin, last login 2025-03-22": "insert into users values mgreen, admin, 2025-03-22",
    "Add vendor to suppliers table, name GreenGoods, contact 555-1122, product Eco-friendly, price tier standard": "insert into suppliers values GreenGoods, 555-1122, Eco-friendly, standard",
    "Insert booking in appointments table, date 2025-04-05, time 13:30, patient Emma Thompson, duration 30": "insert into appointments values 2025-04-05, 13:30, Emma Thompson, 30"
}

create_examples = {
    # Examples where datatypes are specified
    "Create a new students table with INT id, STRING name, INT age, BOOLEAN passing": "create table students INT id, STRING name, INT age, BOOLEAN passing",
    "Make an employees table with INT id, STRING name, STRING department, INT salary, STRING hire_date": "create table employees INT id, STRING name, STRING department, INT salary, STRING hire_date",
    "Set up a products table with INT id, STRING name, FLOAT price, STRING category, INT stock": "create table products INT id, STRING name, FLOAT price, STRING category, INT stock",
    "Create a table called orders with INT id, INT customer_id, FLOAT total, STRING date": "create table orders INT id, INT customer_id, FLOAT total, STRING date",
    "Make a new customers table with INT id, STRING name, STRING email, STRING phone, STRING city": "create table customers INT id, STRING name, STRING email, STRING phone, STRING city",
    
    "Create an inventory table with INT id, STRING name, INT quantity, STRING category, INT reorder_level": "create table inventory INT id, STRING name, INT quantity, STRING category, INT reorder_level",
    "Set up a transactions table with INT id, FLOAT amount, STRING date, STRING status": "create table transactions INT id, FLOAT amount, STRING date, STRING status",
    "Make a users table with STRING username, STRING role, STRING last_login": "create table users STRING username, STRING role, STRING last_login",
    "Create a suppliers table with STRING name, STRING contact, STRING product, STRING price_tier": "create table suppliers STRING name, STRING contact, STRING product, STRING price_tier",
    "Set up an appointments table with STRING date, STRING time, STRING patient, INT duration": "create table appointments STRING date, STRING time, STRING patient, INT duration",
    
    # Examples where datatypes need to be guessed
    "Create a new students table with id, name, age, passing": "create table students INT id, STRING name, INT age, BOOLEAN passing",
    "Make an employees table with id, name, department, salary, hire_date": "create table employees INT id, STRING name, STRING department, INT salary, STRING hire_date",
    "Set up a products table with id, name, price, category, stock": "create table products INT id, STRING name, FLOAT price, STRING category, INT stock",
    "Create a table called orders with id, customer_id, total, date": "create table orders INT id, INT customer_id, FLOAT total, STRING date",
    "Make a new customers table with id, name, email, phone, city": "create table customers INT id, STRING name, STRING email, STRING phone, STRING city",
    
    "Create an inventory table with id, name, quantity, category, reorder_level": "create table inventory INT id, STRING name, INT quantity, STRING category, INT reorder_level",
    "Set up a transactions table with id, amount, date, status": "create table transactions INT id, FLOAT amount, STRING date, STRING status",
    "Make a users table with username, role, last_login": "create table users STRING username, STRING role, STRING last_login",
    "Create a suppliers table with name, contact, product, price_tier": "create table suppliers STRING name, STRING contact, STRING product, STRING price_tier",
    "Set up an appointments table with date, time, patient, duration": "create table appointments STRING date, STRING time, STRING patient, INT duration",
    
    # Mixed examples - some with types, some without
    "Create a courses table with INT id, name, department, INT credits": "create table courses INT id, STRING name, STRING department, INT credits",
    "Make a new projects table with id, STRING title, deadline, BOOLEAN completed": "create table projects INT id, STRING title, STRING deadline, BOOLEAN completed",
    "Set up a books table with ISBN, title, author, FLOAT price, INT pages": "create table books STRING ISBN, STRING title, STRING author, FLOAT price, INT pages",
    "Create a vehicles table with STRING license_plate, make, model, INT year, owner": "create table vehicles STRING license_plate, STRING make, STRING model, INT year, STRING owner",
    "Make an events table with INT id, title, location, date, BOOLEAN is_virtual": "create table events INT id, STRING title, STRING location, STRING date, BOOLEAN is_virtual",
    
    # Different wordings
    "I need a students table with id, name, age, grade": "create table students INT id, STRING name, INT age, STRING grade",
    "Build an employees table with employee_id, full_name, position, department, salary": "create table employees INT employee_id, STRING full_name, STRING position, STRING department, INT salary",
    "Create a database table for products with sku, product_name, price, category": "create table products STRING sku, STRING product_name, FLOAT price, STRING category",
    "Set up a new orders table containing order_number, customer_id, amount, order_date": "create table orders INT order_number, INT customer_id, FLOAT amount, STRING order_date",
    "Make a customers table that has id, first_name, last_name, email, phone": "create table customers INT id, STRING first_name, STRING last_name, STRING email, STRING phone",
    
    # Even more variety
    "Create a movies table with INT id, STRING title, STRING director, INT year, FLOAT rating": "create table movies INT id, STRING title, STRING director, INT year, FLOAT rating",
    "Make a classes table with class_id, class_name, teacher, room_number, start_time": "create table classes INT class_id, STRING class_name, STRING teacher, INT room_number, STRING start_time",
    "Set up a tasks table with task_id, description, due_date, priority, completed": "create table tasks INT task_id, STRING description, STRING due_date, INT priority, BOOLEAN completed",
    "Create a payments table with INT id, STRING method, FLOAT amount, STRING date, STRING status": "create table payments INT id, STRING method, FLOAT amount, STRING date, STRING status",
    "Make a new contacts table with id, first_name, last_name, email, phone, address": "create table contacts INT id, STRING first_name, STRING last_name, STRING email, STRING phone, STRING address",
    
    "Create a flights table with flight_number, origin, destination, departure_time, arrival_time": "create table flights STRING flight_number, STRING origin, STRING destination, STRING departure_time, STRING arrival_time",
    "Make a recipes table with id, name, ingredients, cook_time, calories": "create table recipes INT id, STRING name, STRING ingredients, INT cook_time, INT calories",
    "Set up a songs table with id, title, artist, album, duration, genre": "create table songs INT id, STRING title, STRING artist, STRING album, INT duration, STRING genre",
    "Create a hotels table with hotel_id, name, address, stars, price_range": "create table hotels INT hotel_id, STRING name, STRING address, INT stars, STRING price_range",
    "Make a departments table with dept_id, dept_name, manager, location, budget": "create table departments INT dept_id, STRING dept_name, STRING manager, STRING location, FLOAT budget",
    
    # Additional examples
    "Create a subscriptions table with sub_id, customer_id, plan_type, start_date, end_date, monthly_fee": "create table subscriptions INT sub_id, INT customer_id, STRING plan_type, STRING start_date, STRING end_date, FLOAT monthly_fee",
    "Make a properties table with property_id, address, type, bedrooms, bathrooms, price, is_available": "create table properties INT property_id, STRING address, STRING type, INT bedrooms, INT bathrooms, FLOAT price, BOOLEAN is_available",
    "Set up a tickets table with ticket_id, event_id, seat_number, price, purchaser_name, is_used": "create table tickets INT ticket_id, INT event_id, STRING seat_number, FLOAT price, STRING purchaser_name, BOOLEAN is_used",
    "Create a restaurants table with id, name, cuisine, address, rating, price_level": "create table restaurants INT id, STRING name, STRING cuisine, STRING address, FLOAT rating, INT price_level",
    "Make an accounts table with account_number, account_type, owner_id, balance, open_date": "create table accounts STRING account_number, STRING account_type, INT owner_id, FLOAT balance, STRING open_date"
}

list_examples = {
    "Show me all the tables": "list",
    "What tables do I have in the database?": "list",
    "List all tables": "list",
    "Display all tables in the database": "list",
    "What tables are available?": "list",
    "Give me a list of all tables": "list",
    "Show tables": "list",
    "What's in my database?": "list",
    "I want to see all tables": "list",
    "Can you show me what tables exist?": "list",
    "What tables have I created?": "list",
    "Display database tables": "list",
    "What tables are in the system?": "list",
    "List out all the tables": "list",
    "Show me the tables I have": "list",
    "What tables exist in my database?": "list",
    "Give me all table names": "list",
    "I need to see all tables in the database": "list",
    "Show me what tables I've created": "list",
    "What tables do we have?": "list",
    "Can I see all the tables?": "list",
    "Display a list of all tables": "list",
    "What tables are stored in the database?": "list",
    "I want to know all the tables in my database": "list",
    "Show me everything in my database": "list"
}

show_examples = {
    "Show me the structure of the students table": "show students",
    "What columns are in the employees table?": "show employees",
    "Display the schema for the products table": "show products",
    "Show the structure of the orders table": "show orders",
    "What does the customers table look like?": "show customers",
    "Show me the columns in the inventory table": "show inventory",
    "What's the structure of the transactions table?": "show transactions",
    "Display the columns of the users table": "show users",
    "Show the schema for the suppliers table": "show suppliers",
    "What fields are in the appointments table?": "show appointments",
    "Can you show me what's in the courses table?": "show courses",
    "Display the structure of the projects table": "show projects",
    "What columns make up the books table?": "show books",
    "Show me how the vehicles table is structured": "show vehicles",
    "What's in the events table?": "show events",
    "Show the definition of the movies table": "show movies",
    "What are the columns in the classes table?": "show classes",
    "Show me the structure of the tasks table": "show tasks",
    "What fields does the payments table have?": "show payments",
    "Display columns from the contacts table": "show contacts",
    "Show the structure of the flights table": "show flights",
    "What columns are defined in the recipes table?": "show recipes",
    "Show the schema of the songs table": "show songs",
    "What does the hotels table contain?": "show hotels",
    "Show the structure of the departments table": "show departments"
}