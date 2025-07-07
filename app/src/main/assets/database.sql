CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    full_name TEXT,
    email TEXT,
    phone TEXT,
    address TEXT,
    role TEXT DEFAULT 'user', -- 'admin', 'user'
    profile_image TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    last_login TEXT,
    status INTEGER DEFAULT 1 -- 1: active, 0: inactive
);

-- Categories table
CREATE TABLE categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    image_url TEXT,
    status INTEGER DEFAULT 1, -- 1: active, 0: inactive
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Foods table
CREATE TABLE foods (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_id INTEGER,
    name TEXT NOT NULL,
    description TEXT,
    price REAL NOT NULL,
    image_url TEXT,
    status INTEGER DEFAULT 1, -- 1: available, 0: unavailable
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Orders table
CREATE TABLE orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    total_amount REAL,
    shipping_fee REAL,
    status TEXT DEFAULT 'new', -- 'new', 'processing', 'shipping', 'delivered', 'cancelled'
    payment_method TEXT,
    address TEXT,
    phone TEXT,
    note TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order items table
CREATE TABLE order_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER,
    food_id INTEGER,
    quantity INTEGER,
    price REAL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (food_id) REFERENCES foods(id)
);

-- Order status history table
CREATE TABLE order_status_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER,
    status TEXT NOT NULL,
    note TEXT,
    updated_by INTEGER, -- user_id of admin who updated the status
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- Feedback table
CREATE TABLE feedback (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    type TEXT, -- 'suggestion', 'complaint', 'praise'
    content TEXT,
    status INTEGER DEFAULT 0, -- 0: unresolved, 1: resolved
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Feedback replies table
CREATE TABLE feedback_replies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    feedback_id INTEGER,
    admin_id INTEGER,
    content TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (feedback_id) REFERENCES feedback(id),
    FOREIGN KEY (admin_id) REFERENCES users(id)
);

-- Reviews table
CREATE TABLE reviews (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    food_id INTEGER,
    rating INTEGER, -- 1-5 stars
    comment TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (food_id) REFERENCES foods(id)
);

-- Cart items table
CREATE TABLE cart_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    food_id INTEGER,
    quantity INTEGER DEFAULT 1,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (food_id) REFERENCES foods(id)
);

-- Notifications table
CREATE TABLE notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER, -- NULL nếu là thông báo chung cho tất cả người dùng
    title TEXT,
    content TEXT,
    is_read INTEGER DEFAULT 0, -- 0: unread, 1: read
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- System logs table
CREATE TABLE system_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    log_type TEXT, -- 'error', 'warning', 'info'
    message TEXT,
    stack_trace TEXT,
    user_id INTEGER, -- user who triggered the error, NULL if system error
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);



