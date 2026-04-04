# BookXchange UI Design System & Style Guide

## Overview

BookXchange has been redesigned with a modern, elegant design system specifically crafted for a book marketplace. The entire application now features a cohesive, professional aesthetic with a warm, book-inspired color palette.

## Color Palette

### Primary Colors
- **Primary Brown**: `#6B4423` - Main color, represents books and warmth
- **Dark Brown**: `#5A3A1A` - Darker shade for hover states and emphasis
- **Warm Tan**: `#D4A574` - Accent color, draws attention to important elements
- **Light Tan**: `#C29456` - Softer accent shade

### Secondary Colors
- **Dark Neutral**: `#2C3E50` - Used for navigation and admin elements
- **Light Neutral**: `#1A252F` - Darker shade for dark elements

### Functional Colors
- **Success**: `#27AE60` - For positive actions (buy, purchase, save)
- **Danger**: `#E74C3C` - For destructive actions (delete, logout)
- **Warning**: `#F39C12` - For warnings and alerts
- **Info**: `#3498DB` - For informational messages

### Background Colors
- **Off-white/Cream**: `#FEFDF8` - Main page background
- **Light Background**: `#F9F6F0` - Section backgrounds
- **Subtle Gray**: `#F5F1EA` - Alternative light background
- **Border Color**: `#E0D5C7` - Subtle borders and dividers

## Typography

### Font Family
```
'Segoe UI', 'Helvetica Neue', Arial, sans-serif
```

### Font Sizes
- **H1**: 2.5rem (40px) - Page titles
- **H2**: 2rem (32px) - Section headers
- **H3**: 1.5rem (24px) - Subsection headers
- **H4**: 1.25rem (20px) - Card titles
- **H5**: 1.1rem (18px) - Minor headers
- **H6**: 1rem (16px) - Standard headers
- **Body**: 1rem (16px) - Standard text
- **Small**: 0.9rem (14px) - Secondary text
- **Extra Small**: 0.8rem (13px) - Helper text

### Font Weights
- **Light**: 300 - Subtle text
- **Regular**: 400 - Body text
- **Medium**: 500 - Slightly emphasized
- **Semi-bold**: 600 - Important labels
- **Bold**: 700 - Headers and emphasis

### Line Height
- Default: 1.6 - Comfortable reading
- Headings: 1.2 - Compact headers

## Components

### Buttons

#### Primary Button
```html
<button class="btn btn-primary">Action</button>
```
- Background: Gradient from brown to dark brown
- Color: White
- Padding: 0.75rem 1.5rem
- Border-radius: 6px
- Hover effect: Lift animation + shadow

#### Secondary Button
```html
<button class="btn btn-secondary">Secondary Action</button>
```
- Background: Dark neutral
- Color: White
- Hover: Darker shade with lift

#### Success Button
```html
<button class="btn btn-success">Success Action</button>
```
- Background: Green (#27AE60)
- Color: White
- Used for: Buy, Purchase, Save

#### Danger Button
```html
<button class="btn btn-danger">Delete</button>
```
- Background: Red (#E74C3C)
- Color: White
- Used for: Delete, Logout

#### Outline Button
```html
<button class="btn btn-outline">Outline</button>
```
- Border: 2px solid primary brown
- Color: Primary brown
- Background: Transparent
- Hover: Fills with brown

### Forms

#### Input Fields
- Border: 2px solid `#E0D5C7`
- Border-radius: 6px
- Padding: 0.75rem
- Background: `#FEFDF8`
- Focus: Border changes to tan (`#D4A574`) with subtle shadow

#### Labels
- Font-weight: 600
- Font-size: 0.95rem
- Color: `#2C3E50`
- Margin-bottom: 0.6rem

#### Text Areas
- Same as input fields
- Min-height: 120px
- Resize: Vertical only

### Cards

#### Standard Card
```html
<div class="card">
    <div class="card-body">Content</div>
</div>
```
- Background: White
- Border: 1px solid `#E0D5C7`
- Border-radius: 8px
- Shadow: 0 2px 8px rgba(0,0,0,0.08)
- Hover: Lift effect with enhanced shadow

#### Card with Header
```html
<div class="card">
    <div class="card-header">Header</div>
    <div class="card-body">Content</div>
</div>
```
- Header background: Light gradient
- Header border: 2px solid `#E0D5C7`

### Alerts

#### Alert Types
- **Success**: Green background with green text
- **Danger**: Red background with red text
- **Warning**: Orange/yellow background
- **Info**: Blue background

#### Alert Structure
```html
<div class="alert alert-success">
    Message here
</div>
```
- Border-left: 4px solid (matching color)
- Padding: 1rem 1.5rem
- Border-radius: 6px
- Background: Light tint of the color

### Navigation

#### Navbar
- Background: Gradient from brown to dark brown
- Color: White
- Sticky positioning
- Box-shadow: 0 4px 12px rgba(0,0,0,0.1)
- Padding: 1.2rem 0

#### Navigation Links
- Color: White
- Font-weight: 500
- Hover: Changes to tan color
- Transition: 0.3s ease

### Tabs

#### Tab Navigation
```html
<div class="tabs">
    <button class="tab active">Tab 1</button>
    <button class="tab">Tab 2</button>
</div>
```
- Border-bottom: 2px solid `#E0D5C7`
- Active tab: Border-bottom color becomes tan, text color becomes brown
- Padding: 1rem 1.5rem per tab

## Spacing & Layout

### Margin/Padding Scale
- **px-1/mt-1**: 0.5rem (8px)
- **px-2/mt-2**: 1rem (16px)
- **px-3/mt-3**: 1.5rem (24px)
- **px-4/mt-4**: 2rem (32px)

### Container
- Max-width: 1200px
- Padding: 0 2rem on desktop, 0 1rem on mobile
- Centered with auto margins

### Grid
- Gap: 2rem between items
- Responsive: Adapts from 1 column on mobile to auto-fit on desktop

## Responsive Design

### Breakpoints
- **Mobile**: < 768px
  - Single column layouts
  - Reduced padding/margins
  - Full-width cards
  - Smaller font sizes

- **Tablet**: 768px - 1024px
  - 2-column grids
  - Standard spacing

- **Desktop**: > 1024px
  - 3-4 column grids
  - Full spacing and typography

## Animations & Transitions

### Button Hover
- Translation: -2px (lift effect)
- Shadow: Enhanced box-shadow
- Duration: 0.3s ease

### Card Hover
- Translation: -8px (more pronounced lift)
- Shadow: 0 12px 24px rgba(0,0,0,0.15)
- Duration: 0.3s ease

### Input Focus
- Border-color change to tan
- Box-shadow: 0 0 0 3px rgba(212, 165, 116, 0.1)
- Background: White
- Duration: 0.3s ease

### Link Hover
- Color: Changes to tan
- Duration: 0.3s ease

## Book Cards

### Layout
```html
<div class="book-card">
    <div class="book-cover">📚</div>
    <div class="book-info">
        <h3>Title</h3>
        <p class="book-author">Author</p>
        <p class="book-price">$XX.XX</p>
        <button class="btn">Action</button>
    </div>
</div>
```

### Styling
- Book cover background: Gradient from light to lighter
- Cover text color: Tan
- Cover emoji: 3.5rem size
- Card borders: 1px solid border color
- Hover: Lift effect with shadow

## Dashboard Layout

### Header Section
- Background: White with border
- Contains: Title, user info, role switch button
- Padding: 2rem
- Border-radius: 8px

### Tab System
- Navigation: Horizontal tabs with underline indicator
- Active tab: Brown text with tan underline
- Inactive: Gray text with gray underline

### Content Areas
- Background: Off-white
- Padding: 2rem
- Items: Grid layout with 250px minimum width

## Best Practices

1. **Color Usage**
   - Use primary brown for main actions
   - Use tan for highlights and secondary actions
   - Use green for positive actions (purchase)
   - Use red for destructive actions (delete)

2. **Typography**
   - Always use the font family specified
   - Maintain proper hierarchy with font sizes
   - Use line-height of 1.6 for body text

3. **Spacing**
   - Use consistent spacing from the scale
   - Maintain 2rem gap between grid items
   - Use 1.5rem margins for section separation

4. **Shadows**
   - Light shadows: 0 2px 8px rgba(0,0,0,0.08)
   - Medium shadows: 0 8px 24px rgba(0,0,0,0.12)
   - Heavy shadows: 0 12px 24px rgba(0,0,0,0.15)

5. **Borders**
   - Use `#E0D5C7` for subtle borders
   - Border-radius: 6px for inputs, 8px for cards, 12px for large areas

6. **Transitions**
   - Default: 0.3s ease
   - Used for: Hover states, focus states, animations

## Implementation

### Including Global Styles
All templates should include:
```html
<link rel="stylesheet" th:href="@{/css/global.css}">
```

### Using Components
- Use predefined classes for buttons, forms, cards
- Avoid inline styles
- Override only when necessary using specific selectors

### Customization
- Extend the design system by adding new utility classes
- Maintain color consistency
- Follow spacing and typography rules

## Files Reference

- **Global Stylesheet**: `src/main/resources/templates/shared/global.css`
- **Landing Page**: `src/main/resources/templates/landing.html`
- **Login Page**: `src/main/resources/templates/auth/login.html`
- **Register Page**: `src/main/resources/templates/auth/register.html`
- **Buyer Dashboard**: `src/main/resources/templates/dashboard/buyer-dashboard.html`
- **Seller Dashboard**: `src/main/resources/templates/dashboard/seller-dashboard.html`
- **Admin Dashboard**: `src/main/resources/templates/dashboard/admin-dashboard.html`
- **Book Details**: `src/main/resources/templates/books/book-detail.html`
- **User Profile**: `src/main/resources/templates/user/profile.html`

---

**Design System Version**: 1.0
**Last Updated**: April 2026
**Designed for**: BookXchange - Modern Book Marketplace
