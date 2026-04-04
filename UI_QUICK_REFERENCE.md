# BookXchange UI - Quick Reference Guide

## 🎨 Color Palette Quick Reference

```css
/* Primary (Brown - Book Theme) */
#6B4423   /* Primary Brown */
#5A3A1A   /* Dark Brown */
#D4A574   /* Warm Tan Accent */
#C29456   /* Light Tan */

/* Functional */
#27AE60   /* Success/Green */
#E74C3C   /* Danger/Red */
#F39C12   /* Warning/Orange */
#3498DB   /* Info/Blue */

/* Neutral & Background */
#2C3E50   /* Dark Neutral */
#FEFDF8   /* Off-white Background */
#F9F6F0   /* Light Background */
#E0D5C7   /* Border Color */
```

## 📐 Common Sizing

```
Buttons:     0.75rem padding, 6px border-radius
Cards:       8px border-radius, 2rem padding
Inputs:      0.75rem padding, 6px border-radius
Shadows:     0 2px 8px rgba(0,0,0,0.08) [light]
Transitions: 0.3s ease [default]
Spacing:     2rem between sections
```

## 🔘 Button Classes

```html
<!-- Primary -->
<button class="btn">Default</button>
<button class="btn btn-primary">Primary</button>

<!-- Other Types -->
<button class="btn btn-success">Success</button>
<button class="btn btn-danger">Danger</button>
<button class="btn btn-secondary">Secondary</button>
<button class="btn btn-outline">Outline</button>

<!-- Sizes (add to existing) -->
<button class="btn" style="padding: 1rem 2rem;">Large</button>
```

## 📝 Form Elements

```html
<!-- Input -->
<div class="form-group">
    <label>Label</label>
    <input type="text" placeholder="Enter text">
</div>

<!-- Textarea -->
<div class="form-group">
    <label>Label</label>
    <textarea placeholder="Enter text"></textarea>
</div>

<!-- Select -->
<div class="form-group">
    <label>Label</label>
    <select>
        <option>Option 1</option>
    </select>
</div>
```

## 🃏 Card Components

```html
<!-- Basic Card -->
<div class="card">
    <div class="card-body">
        Content here
    </div>
</div>

<!-- Card with Header/Footer -->
<div class="card">
    <div class="card-header">Header</div>
    <div class="card-body">Content</div>
    <div class="card-footer">Footer</div>
</div>
```

## 🚨 Alert Messages

```html
<!-- Success -->
<div class="alert alert-success">Success message</div>

<!-- Danger -->
<div class="alert alert-danger">Error message</div>

<!-- Warning -->
<div class="alert alert-warning">Warning message</div>

<!-- Info -->
<div class="alert alert-info">Information</div>
```

## 📌 Utility Classes

```html
<!-- Margins -->
<div class="mt-1">margin-top: 0.5rem</div>
<div class="mt-2">margin-top: 1rem</div>
<div class="mt-3">margin-top: 1.5rem</div>
<div class="mt-4">margin-top: 2rem</div>

<div class="mb-1">margin-bottom: 0.5rem</div>
<div class="mb-2">margin-bottom: 1rem</div>

<!-- Padding -->
<div class="p-1">padding: 0.5rem</div>
<div class="p-2">padding: 1rem</div>
<div class="p-3">padding: 1.5rem</div>
<div class="p-4">padding: 2rem</div>

<!-- Flexbox -->
<div class="d-flex">display: flex</div>
<div class="d-flex justify-between">justify-content: space-between</div>
<div class="d-flex align-center">align-items: center</div>

<!-- Gaps -->
<div class="d-flex gap-1">gap: 0.5rem</div>
<div class="d-flex gap-2">gap: 1rem</div>
<div class="d-flex gap-3">gap: 1.5rem</div>

<!-- Text -->
<div class="text-center">text-align: center</div>
<div class="text-muted">Muted text color</div>
<div class="text-primary">Primary color text</div>
<div class="text-success">Success color text</div>
<div class="text-danger">Danger color text</div>
```

## 🎯 Common Layouts

### Navigation
```html
<nav>
    <div class="container">
        <h1>BookXchange</h1>
        <div class="nav-right">
            <a href="/login">Login</a>
            <a href="/register" class="btn btn-primary">Register</a>
        </div>
    </div>
</nav>
```

### Hero Section
```html
<section class="hero" style="background: linear-gradient(135deg, #6B4423 0%, #5A3A1A 100%); color: white; padding: 6rem 2rem; text-align: center;">
    <div class="container">
        <h2>Your Heading</h2>
        <p>Your description</p>
        <a href="#" class="btn btn-primary">Call to Action</a>
    </div>
</section>
```

### Grid of Cards
```html
<div class="grid grid-3">
    <div class="card">
        <div class="card-body">Item 1</div>
    </div>
    <div class="card">
        <div class="card-body">Item 2</div>
    </div>
    <div class="card">
        <div class="card-body">Item 3</div>
    </div>
</div>
```

### Dashboard Header
```html
<div class="dashboard-header">
    <div>
        <h2>📚 Dashboard Title</h2>
        <p>Subtitle or status</p>
    </div>
    <div class="user-info">
        <p>User: John Doe</p>
        <p>Email: john@example.com</p>
        <button class="role-switch">Switch Role</button>
    </div>
</div>
```

## 🎨 Styling Patterns

### Hover Effects
```css
/* Button Lift */
transition: all 0.3s ease;
&:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(107, 68, 35, 0.3);
}

/* Card Lift */
transform: translateY(-8px);
box-shadow: 0 12px 24px rgba(0,0,0,0.15);

/* Input Focus */
border-color: #D4A574;
box-shadow: 0 0 0 3px rgba(212, 165, 116, 0.1);
```

## 📱 Responsive Patterns

### Mobile First
```html
<!-- Single Column -->
<div class="grid grid-2">
    <!-- Auto-fits, 1 column on mobile -->
</div>

<!-- Or use flex for better control -->
<div class="d-flex" style="flex-wrap: wrap; gap: 1rem;">
    <div style="flex: 1; min-width: 200px;">Item 1</div>
    <div style="flex: 1; min-width: 200px;">Item 2</div>
</div>
```

## 🚀 Common Components

### Book Card
```html
<div class="book-card">
    <div class="book-cover">📚</div>
    <div class="book-info">
        <h3>Book Title</h3>
        <p class="book-author">Author Name</p>
        <p class="book-price">$19.99</p>
        <button class="btn btn-success">Buy Now</button>
    </div>
</div>
```

### Tab Navigation
```html
<div class="tabs">
    <button class="tab active" onclick="switchTab(event, 'tab1')">Tab 1</button>
    <button class="tab" onclick="switchTab(event, 'tab2')">Tab 2</button>
</div>
<div id="tab1" class="tab-content active">Content 1</div>
<div id="tab2" class="tab-content">Content 2</div>
```

### Profile Section
```html
<div class="card">
    <div class="card-body">
        <div style="text-align: center;">
            <div class="picture-placeholder">👤</div>
            <h3>Username</h3>
            <p class="text-muted">email@example.com</p>
            <span class="badge-role buyer">Buyer</span>
        </div>
    </div>
</div>
```

## 🔗 Template Structure

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Page Title</title>
    <link rel="stylesheet" th:href="@{/css/global.css}">
    <style>
        /* Page-specific styles only */
    </style>
</head>
<body>
    <nav>
        <!-- Navigation -->
    </nav>
    
    <div class="container">
        <!-- Content -->
    </div>
    
    <footer>
        <!-- Footer -->
    </footer>
</body>
</html>
```

## ⚠️ Dos and Don'ts

### ✅ Do
- Use predefined classes
- Include global.css in every template
- Follow the color palette
- Use 2rem for section gaps
- Use 0.3s transitions
- Create reusable components

### ❌ Don't
- Use inline styles (except for rare cases)
- Mix different color schemes
- Use non-standard spacing
- Create custom button styles
- Use different font families
- Add random shadows or gradients

## 🎓 Best Practices

1. **Always include** `<link rel="stylesheet" th:href="@{/css/global.css}">`
2. **Use classes** instead of inline styles
3. **Follow spacing** - use utility classes (mt-*, mb-*, p-*)
4. **Keep buttons consistent** - use .btn classes
5. **Color choices** - follow the palette defined
6. **Test responsive** - check mobile, tablet, desktop views
7. **Maintain hierarchy** - use h1-h6 properly

---

**For detailed information**, see `UI_DESIGN_SYSTEM.md`

**Last Updated**: April 4, 2026
