# Configuration Service UI - Quick Start Guide

## Accessing the Configuration UI

### Development Environment
1. **Start the frontend development server**:
   ```bash
   cd frontend/sms-frontend
   npm run dev
   ```
   Access at: http://localhost:5173

2. **Navigate to Configuration**:
   - Click "Configuration" in the top navigation menu
   - Or directly visit: http://localhost:5173/configuration

## Managing Configuration Settings

### View Settings by Category

The Configuration page displays settings organized in three categories:

1. **GENERAL** - General school settings (timezone, date format, etc.)
2. **ACADEMIC** - Academic-related settings (academic year, student age limits, etc.)
3. **FINANCIAL** - Financial settings (currency code, payment terms, etc.)

**How to switch categories**:
- Click on the category tabs at the top of the settings table
- The number badge shows how many settings exist in each category

### Add a New Setting

1. Click the **"Add Setting"** button (top right)
2. Fill in the form:
   - **Category**: Select from dropdown (GENERAL/ACADEMIC/FINANCIAL)
   - **Key**: Enter in UPPERCASE_SNAKE_CASE format (e.g., `SCHOOL_TIMEZONE`)
     - Must start with a letter
     - Use only uppercase letters, numbers, and underscores
   - **Value**: Enter the setting value (e.g., `Asia/Kolkata`)
   - **Description**: Optional description (max 500 characters)
3. Click **"Create"**
4. Success notification appears, and the new setting is added to the table

**Example Settings**:
```
Category: GENERAL
Key: SCHOOL_TIMEZONE
Value: Asia/Kolkata
Description: Default timezone for the school

Category: ACADEMIC
Key: MIN_STUDENT_AGE
Value: 3
Description: Minimum student age for admission

Category: FINANCIAL
Key: CURRENCY_CODE
Value: INR
Description: Currency code (ISO 4217)
```

### Edit a Setting

1. Find the setting in the table
2. Click the **"Edit"** button in the Actions column
3. Modify the **Value** or **Description** (Category and Key cannot be changed)
4. Click **"Update"**
5. Success notification appears

**Note**: The system uses optimistic locking. If another user modified the setting simultaneously, you'll see an error and need to refresh.

### Delete a Setting

1. Find the setting in the table
2. Click the **"Delete"** button in the Actions column
3. Confirm deletion in the dialog
4. Success notification appears

**Warning**: This action cannot be undone. Make sure you want to delete the setting.

## Managing School Profile

### View School Profile

1. From the Configuration page, click **"School Profile"** button (top right)
2. Or directly visit: http://localhost:5173/configuration/school-profile
3. View all school information in read-only mode

**Displayed Information**:
- School Name
- School Code
- Phone
- Email
- Logo Path (if set)
- Address
- Last Updated timestamp
- Updated By user

### Edit School Profile

1. On the School Profile page, click **"Edit Profile"**
2. Form becomes editable
3. Update any of the following fields:
   - **School Name**: Required, max 200 characters
   - **School Code**: Required, 3-20 uppercase alphanumeric characters
   - **Phone**: Required, 10-15 characters (numbers, +, -, (, ))
   - **Email**: Required, valid email format
   - **Logo Path**: Optional, file path to logo image
   - **Address**: Required, max 500 characters (multiline)
4. Click **"Save Changes"**
5. Or click **"Cancel"** to discard changes

**Validation Rules**:
- School Code auto-converts to uppercase
- Phone must match pattern: digits, +, -, (, )
- Email must be valid format

### Navigate Back

- Click **"Back to Configuration"** to return to the settings page

## UI Features

### Real-time Feedback

**Success Notifications** (green toast):
- Setting created successfully
- Setting updated successfully
- Setting deleted successfully
- School profile updated successfully

**Error Notifications** (red toast):
- Failed to create/update/delete setting
- Validation errors
- Network errors
- Backend errors with detail message

### Loading States

- **Skeleton loader** while fetching data
- **Spinning button** during form submission
- **Disabled inputs** during API calls

### Validation

**Client-side Validation**:
- Required fields marked with red asterisk (*)
- Pattern validation for keys and codes
- Email format validation
- Character count limits
- HTML5 validation attributes

**Server-side Validation**:
- Duplicate key detection (409 Conflict)
- Business rule validation
- Concurrent update detection (optimistic locking)

### Empty States

- Shows helpful message when no settings exist in a category
- Prompts user to create a setting

## Keyboard Shortcuts

- **Tab**: Navigate between form fields
- **Enter**: Submit form
- **Escape**: Close dialog
- **Click outside**: Close dialog

## Common Operations

### Bulk Setup (Initial Configuration)

To set up a new school, add these common settings:

**GENERAL Category**:
```
SCHOOL_TIMEZONE = Asia/Kolkata
DATE_FORMAT = dd-MM-yyyy
TIME_FORMAT = HH:mm:ss
DEFAULT_LANGUAGE = en
```

**ACADEMIC Category**:
```
CURRENT_ACADEMIC_YEAR = 2025-2026
MIN_STUDENT_AGE = 3
MAX_STUDENT_AGE = 18
ACADEMIC_YEAR_START_MONTH = 4
ACADEMIC_YEAR_END_MONTH = 3
```

**FINANCIAL Category**:
```
CURRENCY_CODE = INR
CURRENCY_SYMBOL = ₹
PAYMENT_DUE_DAYS = 30
LATE_FEE_ENABLED = true
```

### Update School Profile

1. School Name: "ABC International School"
2. School Code: "SCH001"
3. Phone: "+91-80-12345678"
4. Email: "info@abcschool.edu.in"
5. Address:
   ```
   123 Education Street
   Bangalore, Karnataka - 560001
   India
   ```

## Troubleshooting

### Error: "Failed to create setting"
- **Check**: Key is unique within the category
- **Check**: Key follows UPPERCASE_SNAKE_CASE pattern
- **Check**: All required fields are filled
- **Check**: Backend service is running

### Error: "Concurrent modification"
- **Cause**: Another user edited the setting
- **Solution**: Refresh the page and try again

### Error: "Internal Server Error"
- **Cause**: Backend issue or database not initialized
- **Solution**: Contact backend team or check backend logs

### Settings not loading
- **Check**: Configuration Service is running (port 8082)
- **Check**: Network connectivity
- **Check**: Browser console for errors
- **Solution**: Refresh the page

### Form validation errors
- **Check**: All required fields have values
- **Check**: Key matches pattern (UPPERCASE_SNAKE_CASE)
- **Check**: School Code matches pattern (3-20 alphanumeric)
- **Check**: Email is valid format
- **Check**: Phone matches pattern

## API Endpoints Reference

All requests go through the Configuration Service (port 8082):

### Settings
- `GET /configurations/settings` - Get all settings
- `POST /configurations/settings` - Create setting
- `GET /configurations/settings/:id` - Get setting by ID
- `PUT /configurations/settings/:id` - Update setting
- `DELETE /configurations/settings/:id` - Delete setting
- `GET /configurations/settings/category/:category` - Get by category

### School Profile
- `GET /configurations/school-profile` - Get school profile
- `PUT /configurations/school-profile` - Update school profile

## Tips and Best Practices

### Setting Keys
- Use descriptive names
- Follow UPPERCASE_SNAKE_CASE convention
- Group related settings with prefixes (e.g., `EMAIL_*`, `SMS_*`)
- Avoid spaces and special characters

### Setting Values
- Keep values simple and parsable
- Use standard formats (ISO dates, ISO currency codes)
- Document complex values in description
- Consider future parsing needs

### Descriptions
- Write clear, concise descriptions
- Explain purpose and impact
- Include example values if helpful
- Note any dependencies or constraints

### School Profile
- Keep information up-to-date
- Use consistent formatting
- Update contact details regularly
- Upload school logo for branding

### Organization
- Group related settings in appropriate categories
- Delete obsolete settings
- Document custom settings
- Maintain consistency across environments

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Mobile Responsive

The UI is fully responsive and works on:
- Desktop (1024px+)
- Tablet (768px - 1023px)
- Mobile (< 768px)

On mobile:
- Tabs stack vertically
- Forms become single-column
- Tables scroll horizontally
- Buttons stack for better touch targets

---

## Need Help?

- Check backend logs for server errors
- Review browser console for client errors
- Verify backend services are running
- Contact development team for support

---

**Last Updated**: 2025-11-18
**Version**: 1.0
