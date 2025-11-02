# Comprehensive API Testing Script for School Management System
# Tests all 65 endpoints across 6 controllers

$baseUrl = "http://localhost:8080/api"
$testResults = @()
$totalTests = 0
$passedTests = 0
$failedTests = 0

# Color codes for output
function Write-Success { Write-Host $args -ForegroundColor Green }
function Write-Error-Custom { Write-Host $args -ForegroundColor Red }
function Write-Info { Write-Host $args -ForegroundColor Cyan }
function Write-Warning-Custom { Write-Host $args -ForegroundColor Yellow }

# Test helper function
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Description,
        [object]$Body = $null,
        [int[]]$ExpectedStatusCodes = @(200, 201)
    )

    $script:totalTests++
    Write-Info "`n[$script:totalTests] Testing: $Description"
    Write-Host "  Method: $Method | URL: $Url" -ForegroundColor Gray

    try {
        $headers = @{
            "Content-Type" = "application/json"
            "Accept" = "application/json"
        }

        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $headers
            ErrorAction = "Stop"
        }

        if ($Body -ne $null) {
            $jsonBody = $Body | ConvertTo-Json -Depth 10
            $params.Body = $jsonBody
            Write-Host "  Body: $jsonBody" -ForegroundColor Gray
        }

        $response = Invoke-WebRequest @params
        $statusCode = $response.StatusCode

        if ($ExpectedStatusCodes -contains $statusCode) {
            Write-Success "  PASSED - Status: $statusCode"
            $script:passedTests++
            $script:testResults += [PSCustomObject]@{
                Test = $Description
                Method = $Method
                Endpoint = $Url
                Status = "PASSED"
                StatusCode = $statusCode
                Response = $response.Content
            }
            return $response.Content | ConvertFrom-Json
        } else {
            Write-Error-Custom "  FAILED - Unexpected status: $statusCode"
            $script:failedTests++
            $script:testResults += [PSCustomObject]@{
                Test = $Description
                Method = $Method
                Endpoint = $Url
                Status = "FAILED"
                StatusCode = $statusCode
                Response = $response.Content
            }
            return $null
        }
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        if ($ExpectedStatusCodes -contains $statusCode) {
            Write-Success "  PASSED - Status: $statusCode (Expected Error)"
            $script:passedTests++
            $script:testResults += [PSCustomObject]@{
                Test = $Description
                Method = $Method
                Endpoint = $Url
                Status = "PASSED"
                StatusCode = $statusCode
                Response = $_.Exception.Message
            }
            return $null
        } else {
            Write-Error-Custom "  FAILED - Error: $($_.Exception.Message)"
            $script:failedTests++
            $script:testResults += [PSCustomObject]@{
                Test = $Description
                Method = $Method
                Endpoint = $Url
                Status = "FAILED"
                StatusCode = $statusCode
                Response = $_.Exception.Message
            }
            return $null
        }
    }
}

Write-Host "`n========================================" -ForegroundColor Magenta
Write-Host "SCHOOL MANAGEMENT SYSTEM - API TESTS" -ForegroundColor Magenta
Write-Host "========================================`n" -ForegroundColor Magenta

# ============================================================================
# 1. CLASS CONTROLLER TESTS (10 endpoints)
# ============================================================================
Write-Host "`n=== CLASS CONTROLLER TESTS ===" -ForegroundColor Yellow

# Create Class #1
$classData1 = @{
    classNumber = 1
    section = "A"
    academicYear = "2024-2025"
    capacity = 50
    classTeacher = "Mrs. Smith"
    roomNumber = "101"
}
$class1 = Test-Endpoint -Method "POST" -Url "$baseUrl/classes" -Description "Create Class 1-A" -Body $classData1 -ExpectedStatusCodes @(201)

# Create Class #2
$classData2 = @{
    classNumber = 2
    section = "B"
    academicYear = "2024-2025"
    capacity = 45
    classTeacher = "Mr. Johnson"
    roomNumber = "202"
}
$class2 = Test-Endpoint -Method "POST" -Url "$baseUrl/classes" -Description "Create Class 2-B" -Body $classData2 -ExpectedStatusCodes @(201)

# Create Class #3 (Almost Full)
$classData3 = @{
    classNumber = 3
    section = "A"
    academicYear = "2024-2025"
    capacity = 10
    classTeacher = "Ms. Davis"
    roomNumber = "301"
}
$class3 = Test-Endpoint -Method "POST" -Url "$baseUrl/classes" -Description "Create Class 3-A (Small Capacity)" -Body $classData3 -ExpectedStatusCodes @(201)

# Get class by ID
if ($class1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/classes/$($class1.id)" -Description "Get Class by ID"
}

# Get all classes
Test-Endpoint -Method "GET" -Url "$baseUrl/classes" -Description "Get All Classes"

# Get all classes by academic year
Test-Endpoint -Method "GET" -Url "$baseUrl/classes?academicYear=2024-2025" -Description "Get Classes by Academic Year"

# Get classes by class number
Test-Endpoint -Method "GET" -Url "$baseUrl/classes/by-number?classNumber=1&academicYear=2024-2025" -Description "Get Classes by Class Number"

# Get classes with available seats
Test-Endpoint -Method "GET" -Url "$baseUrl/classes/available?academicYear=2024-2025" -Description "Get Classes with Available Seats"

# Get total students
Test-Endpoint -Method "GET" -Url "$baseUrl/classes/total-students?academicYear=2024-2025" -Description "Get Total Students"

# Check if class exists
Test-Endpoint -Method "GET" -Url "$baseUrl/classes/exists?classNumber=1&section=A&academicYear=2024-2025" -Description "Check if Class Exists"

# Update class
if ($class1 -ne $null) {
    $updateData = @{
        classNumber = 1
        section = "A"
        academicYear = "2024-2025"
        capacity = 55
        classTeacher = "Mrs. Smith-Updated"
        roomNumber = "101"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/classes/$($class1.id)" -Description "Update Class" -Body $updateData
}

# ============================================================================
# 2. STUDENT CONTROLLER TESTS (8 endpoints)
# ============================================================================
Write-Host "`n=== STUDENT CONTROLLER TESTS ===" -ForegroundColor Yellow

# Create Student #1
$studentData1 = @{
    firstName = "John"
    lastName = "Doe"
    dateOfBirth = "2010-05-15"
    address = "123 Main St, City"
    mobile = "9876543210"
    religion = "Christian"
    caste = "General"
    identifyingMarks = "Mole on left cheek"
    motherName = "Jane Doe"
    fatherName = "James Doe"
    classId = if ($class1 -ne $null) { $class1.id } else { 1 }
    enrollmentDate = "2024-04-01"
    status = "ACTIVE"
}
$student1 = Test-Endpoint -Method "POST" -Url "$baseUrl/students" -Description "Create Student John Doe" -Body $studentData1 -ExpectedStatusCodes @(201)

# Create Student #2
$studentData2 = @{
    firstName = "Emily"
    lastName = "Smith"
    dateOfBirth = "2011-08-22"
    address = "456 Oak Ave, City"
    mobile = "9123456789"
    religion = "Hindu"
    caste = "OBC"
    identifyingMarks = "Scar on right hand"
    motherName = "Sarah Smith"
    fatherName = "Robert Smith"
    classId = if ($class2 -ne $null) { $class2.id } else { 2 }
    enrollmentDate = "2024-04-01"
    status = "ACTIVE"
}
$student2 = Test-Endpoint -Method "POST" -Url "$baseUrl/students" -Description "Create Student Emily Smith" -Body $studentData2 -ExpectedStatusCodes @(201)

# Create Student #3
$studentData3 = @{
    firstName = "Rahul"
    lastName = "Kumar"
    dateOfBirth = "2009-12-10"
    address = "789 Park Lane, City"
    mobile = "9988776655"
    religion = "Hindu"
    caste = "SC"
    identifyingMarks = "Birth mark on neck"
    motherName = "Priya Kumar"
    fatherName = "Rajesh Kumar"
    classId = if ($class1 -ne $null) { $class1.id } else { 1 }
    enrollmentDate = "2024-04-01"
    status = "ACTIVE"
}
$student3 = Test-Endpoint -Method "POST" -Url "$baseUrl/students" -Description "Create Student Rahul Kumar" -Body $studentData3 -ExpectedStatusCodes @(201)

# Get student by ID
if ($student1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/students/$($student1.id)" -Description "Get Student by ID"
}

# Get all students
Test-Endpoint -Method "GET" -Url "$baseUrl/students" -Description "Get All Students"

# Get students by class
if ($class1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/students?classId=$($class1.id)" -Description "Get Students by Class"
}

# Search students by name
Test-Endpoint -Method "GET" -Url "$baseUrl/students/search?q=John" -Description "Search Students by Name"

# Autocomplete search
Test-Endpoint -Method "GET" -Url "$baseUrl/students/autocomplete?q=98" -Description "Autocomplete Search (Mobile)"

# Get pending fees students (will be empty initially)
Test-Endpoint -Method "GET" -Url "$baseUrl/students/pending-fees" -Description "Get Students with Pending Fees"

# Update student
if ($student1 -ne $null) {
    $updateStudentData = @{
        firstName = "John-Updated"
        lastName = "Doe"
        dateOfBirth = "2010-05-15"
        address = "123 Main St Updated, City"
        mobile = "9876543210"
        religion = "Christian"
        caste = "General"
        identifyingMarks = "Mole on left cheek"
        motherName = "Jane Doe"
        fatherName = "James Doe"
        classId = if ($class1 -ne $null) { $class1.id } else { 1 }
        enrollmentDate = "2024-04-01"
        status = "ACTIVE"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/students/$($student1.id)" -Description "Update Student" -Body $updateStudentData
}

# ============================================================================
# 3. FEE MASTER CONTROLLER TESTS (12 endpoints)
# ============================================================================
Write-Host "`n=== FEE MASTER CONTROLLER TESTS ===" -ForegroundColor Yellow

# Create Fee Master #1 - Tuition
$feeMasterData1 = @{
    feeType = "TUITION"
    amount = 5000.00
    frequency = "MONTHLY"
    applicableFrom = "2024-04-01"
    applicableTo = "2025-03-31"
    description = "Monthly tuition fee for academic year 2024-2025"
    isActive = $true
    academicYear = "2024-2025"
}
$feeMaster1 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-masters" -Description "Create Fee Master - Tuition" -Body $feeMasterData1 -ExpectedStatusCodes @(201)

# Create Fee Master #2 - Library
$feeMasterData2 = @{
    feeType = "LIBRARY"
    amount = 500.00
    frequency = "ANNUAL"
    applicableFrom = "2024-04-01"
    applicableTo = "2025-03-31"
    description = "Annual library fee"
    isActive = $true
    academicYear = "2024-2025"
}
$feeMaster2 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-masters" -Description "Create Fee Master - Library" -Body $feeMasterData2 -ExpectedStatusCodes @(201)

# Create Fee Master #3 - Computer
$feeMasterData3 = @{
    feeType = "COMPUTER"
    amount = 1000.00
    frequency = "QUARTERLY"
    applicableFrom = "2024-04-01"
    applicableTo = "2025-03-31"
    description = "Quarterly computer lab fee"
    isActive = $true
    academicYear = "2024-2025"
}
$feeMaster3 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-masters" -Description "Create Fee Master - Computer" -Body $feeMasterData3 -ExpectedStatusCodes @(201)

# Create Fee Master #4 - Sports (Inactive)
$feeMasterData4 = @{
    feeType = "SPORTS"
    amount = 750.00
    frequency = "ANNUAL"
    applicableFrom = "2024-04-01"
    applicableTo = "2025-03-31"
    description = "Annual sports fee"
    isActive = $false
    academicYear = "2024-2025"
}
$feeMaster4 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-masters" -Description "Create Fee Master - Sports (Inactive)" -Body $feeMasterData4 -ExpectedStatusCodes @(201)

# Get fee master by ID
if ($feeMaster1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-masters/$($feeMaster1.id)" -Description "Get Fee Master by ID"
}

# Get all fee masters
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-masters" -Description "Get All Fee Masters"

# Get fee masters by academic year
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-masters?academicYear=2024-2025" -Description "Get Fee Masters by Academic Year"

# Get fee masters by type
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-masters/by-type/TUITION" -Description "Get Fee Masters by Type (TUITION)"

# Get fee masters by type (active only)
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-masters/by-type/SPORTS?activeOnly=true" -Description "Get Fee Masters by Type - Active Only"

# Get active fee masters
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-masters/active?academicYear=2024-2025" -Description "Get Active Fee Masters"

# Get applicable fee masters
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-masters/applicable" -Description "Get Currently Applicable Fee Masters"

# Get latest fee master by type
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-masters/latest/TUITION" -Description "Get Latest Fee Master by Type"

# Count active fee masters
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-masters/count?academicYear=2024-2025" -Description "Count Active Fee Masters"

# Update fee master
if ($feeMaster1 -ne $null) {
    $updateFeeMasterData = @{
        feeType = "TUITION"
        amount = 5500.00
        frequency = "MONTHLY"
        applicableFrom = "2024-04-01"
        applicableTo = "2025-03-31"
        description = "Updated monthly tuition fee"
        isActive = $true
        academicYear = "2024-2025"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/fee-masters/$($feeMaster1.id)" -Description "Update Fee Master" -Body $updateFeeMasterData
}

# Activate fee master
if ($feeMaster4 -ne $null) {
    Test-Endpoint -Method "PATCH" -Url "$baseUrl/fee-masters/$($feeMaster4.id)/activate" -Description "Activate Fee Master"
}

# Deactivate fee master
if ($feeMaster4 -ne $null) {
    Test-Endpoint -Method "PATCH" -Url "$baseUrl/fee-masters/$($feeMaster4.id)/deactivate" -Description "Deactivate Fee Master"
}

# ============================================================================
# 4. FEE JOURNAL CONTROLLER TESTS (12 endpoints)
# ============================================================================
Write-Host "`n=== FEE JOURNAL CONTROLLER TESTS ===" -ForegroundColor Yellow

# Create Fee Journal #1
$journalData1 = @{
    studentId = if ($student1 -ne $null) { $student1.id } else { 1 }
    month = 4
    year = 2024
    amountDue = 5500.00
    amountPaid = 0.00
    paymentStatus = "PENDING"
    dueDate = "2024-04-10"
    remarks = "April 2024 fee"
}
$journal1 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-journals" -Description "Create Fee Journal - April 2024" -Body $journalData1 -ExpectedStatusCodes @(201)

# Create Fee Journal #2 - Partially Paid
$journalData2 = @{
    studentId = if ($student1 -ne $null) { $student1.id } else { 1 }
    month = 5
    year = 2024
    amountDue = 5500.00
    amountPaid = 2000.00
    paymentStatus = "PARTIAL"
    dueDate = "2024-05-10"
    remarks = "May 2024 fee - Partial payment"
}
$journal2 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-journals" -Description "Create Fee Journal - May 2024 (Partial)" -Body $journalData2 -ExpectedStatusCodes @(201)

# Create Fee Journal #3 - Paid
$journalData3 = @{
    studentId = if ($student2 -ne $null) { $student2.id } else { 2 }
    month = 4
    year = 2024
    amountDue = 5500.00
    amountPaid = 5500.00
    paymentStatus = "PAID"
    dueDate = "2024-04-10"
    paymentDate = "2024-04-08"
    remarks = "April 2024 fee - Fully paid"
}
$journal3 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-journals" -Description "Create Fee Journal - Fully Paid" -Body $journalData3 -ExpectedStatusCodes @(201)

# Create Fee Journal #4 - Overdue
$journalData4 = @{
    studentId = if ($student3 -ne $null) { $student3.id } else { 3 }
    month = 3
    year = 2024
    amountDue = 5000.00
    amountPaid = 0.00
    paymentStatus = "OVERDUE"
    dueDate = "2024-03-10"
    remarks = "March 2024 fee - Overdue"
}
$journal4 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-journals" -Description "Create Fee Journal - Overdue" -Body $journalData4 -ExpectedStatusCodes @(201)

# Get fee journal by ID
if ($journal1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/$($journal1.id)" -Description "Get Fee Journal by ID"
}

# Get all fee journals
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals" -Description "Get All Fee Journals"

# Get journals for student
if ($student1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/student/$($student1.id)" -Description "Get Journals for Student"
}

# Get pending journals for student
if ($student1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/student/$($student1.id)/pending" -Description "Get Pending Journals for Student"
}

# Get journals by month
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/by-month?month=4&year=2024" -Description "Get Journals by Month"

# Get journals by status
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/by-status/PENDING" -Description "Get Journals by Status (PENDING)"

# Get overdue journals
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/overdue" -Description "Get Overdue Journals"

# Get student dues summary
if ($student1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/student/$($student1.id)/summary" -Description "Get Student Dues Summary"
}

# Update fee journal
if ($journal1 -ne $null) {
    $updateJournalData = @{
        studentId = if ($student1 -ne $null) { $student1.id } else { 1 }
        month = 4
        year = 2024
        amountDue = 5500.00
        amountPaid = 1000.00
        paymentStatus = "PARTIAL"
        dueDate = "2024-04-10"
        remarks = "April 2024 fee - Updated with partial payment"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/fee-journals/$($journal1.id)" -Description "Update Fee Journal" -Body $updateJournalData
}

# Record payment
if ($journal2 -ne $null) {
    $paymentData = @{
        amount = 3500.00
    }
    Test-Endpoint -Method "PATCH" -Url "$baseUrl/fee-journals/$($journal2.id)/payment" -Description "Record Payment" -Body $paymentData
}

# ============================================================================
# 5. FEE RECEIPT CONTROLLER TESTS (13 endpoints)
# ============================================================================
Write-Host "`n=== FEE RECEIPT CONTROLLER TESTS ===" -ForegroundColor Yellow

# Create Fee Receipt #1 - Cash Payment
$receiptData1 = @{
    studentId = if ($student1 -ne $null) { $student1.id } else { 1 }
    amount = 5500.00
    paymentDate = "2024-11-01"
    paymentMethod = "CASH"
    monthsPaid = @(4)
    feeBreakdown = @{
        TUITION = 5000.00
        LIBRARY = 500.00
    }
    remarks = "Cash payment for April 2024"
    generatedBy = "Admin"
}
$receipt1 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-receipts" -Description "Generate Fee Receipt - Cash" -Body $receiptData1 -ExpectedStatusCodes @(201)

# Create Fee Receipt #2 - Online Payment
$receiptData2 = @{
    studentId = if ($student2 -ne $null) { $student2.id } else { 2 }
    amount = 6000.00
    paymentDate = "2024-11-01"
    paymentMethod = "ONLINE"
    transactionId = "TXN123456789"
    monthsPaid = @(4, 5)
    feeBreakdown = @{
        TUITION = 5500.00
        LIBRARY = 500.00
    }
    remarks = "Online payment for April-May 2024"
    generatedBy = "Admin"
}
$receipt2 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-receipts" -Description "Generate Fee Receipt - Online" -Body $receiptData2 -ExpectedStatusCodes @(201)

# Create Fee Receipt #3 - Cheque Payment
$receiptData3 = @{
    studentId = if ($student3 -ne $null) { $student3.id } else { 3 }
    amount = 5500.00
    paymentDate = "2024-11-02"
    paymentMethod = "CHEQUE"
    chequeNumber = "CHQ789456"
    bankName = "State Bank"
    monthsPaid = @(4)
    feeBreakdown = @{
        TUITION = 5000.00
        COMPUTER = 500.00
    }
    remarks = "Cheque payment for April 2024"
    generatedBy = "Admin"
}
$receipt3 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-receipts" -Description "Generate Fee Receipt - Cheque" -Body $receiptData3 -ExpectedStatusCodes @(201)

# Get receipt by ID
if ($receipt1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/$($receipt1.id)" -Description "Get Fee Receipt by ID"
}

# Get receipt by receipt number
if ($receipt1 -ne $null -and $receipt1.receiptNumber -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/number/$($receipt1.receiptNumber)" -Description "Get Receipt by Receipt Number"
}

# Get all receipts
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts" -Description "Get All Fee Receipts"

# Get receipts for student
if ($student1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/student/$($student1.id)" -Description "Get Receipts for Student"
}

# Get receipts by date range
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/by-date?startDate=2024-11-01&endDate=2024-11-02" -Description "Get Receipts by Date Range"

# Get receipts by payment method
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/by-method/CASH" -Description "Get Receipts by Payment Method"

# Get today's receipts
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/today" -Description "Get Today's Receipts"

# Get total collection for date range
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/collection?startDate=2024-11-01&endDate=2024-11-02" -Description "Get Total Collection"

# Get collection by payment method
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/collection/by-method?paymentMethod=CASH&startDate=2024-11-01&endDate=2024-11-02" -Description "Get Collection by Payment Method"

# Get collection summary
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/collection/summary?startDate=2024-11-01&endDate=2024-11-02" -Description "Get Collection Summary"

# Count receipts for student
if ($student1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/count/$($student1.id)" -Description "Count Receipts for Student"
}

# ============================================================================
# 6. SCHOOL CONFIG CONTROLLER TESTS (10 endpoints)
# ============================================================================
Write-Host "`n=== SCHOOL CONFIG CONTROLLER TESTS ===" -ForegroundColor Yellow

# Create Config #1
$configData1 = @{
    configKey = "SCHOOL_NAME"
    configValue = "ABC Public School"
    category = "GENERAL"
    description = "Official school name"
    isEditable = $true
    dataType = "STRING"
}
$config1 = Test-Endpoint -Method "POST" -Url "$baseUrl/school-config" -Description "Create Config - School Name" -Body $configData1 -ExpectedStatusCodes @(201)

# Create Config #2
$configData2 = @{
    configKey = "MAX_STUDENTS_PER_CLASS"
    configValue = "50"
    category = "ACADEMIC"
    description = "Maximum students allowed per class"
    isEditable = $true
    dataType = "INTEGER"
}
$config2 = Test-Endpoint -Method "POST" -Url "$baseUrl/school-config" -Description "Create Config - Max Students" -Body $configData2 -ExpectedStatusCodes @(201)

# Create Config #3
$configData3 = @{
    configKey = "ENABLE_SMS_NOTIFICATIONS"
    configValue = "true"
    category = "NOTIFICATION"
    description = "Enable SMS notifications"
    isEditable = $true
    dataType = "BOOLEAN"
}
$config3 = Test-Endpoint -Method "POST" -Url "$baseUrl/school-config" -Description "Create Config - SMS Notifications" -Body $configData3 -ExpectedStatusCodes @(201)

# Create Config #4 - System Config
$configData4 = @{
    configKey = "SYSTEM_VERSION"
    configValue = "1.0.0"
    category = "SYSTEM"
    description = "System version number"
    isEditable = $false
    dataType = "STRING"
}
$config4 = Test-Endpoint -Method "POST" -Url "$baseUrl/school-config" -Description "Create Config - System Version (Not Editable)" -Body $configData4 -ExpectedStatusCodes @(201)

# Get config by ID
if ($config1 -ne $null) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/school-config/$($config1.id)" -Description "Get Config by ID"
}

# Get config by key
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config/key/SCHOOL_NAME" -Description "Get Config by Key"

# Get config value only
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config/value/SCHOOL_NAME" -Description "Get Config Value Only"

# Get all configs
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config" -Description "Get All Configs"

# Get configs by category
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config?category=ACADEMIC" -Description "Get Configs by Category"

# Get editable configs only
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config/editable" -Description "Get Editable Configs Only"

# Check if config exists
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config/exists/SCHOOL_NAME" -Description "Check if Config Exists"

# Update config (full update)
if ($config1 -ne $null) {
    $updateConfigData = @{
        configKey = "SCHOOL_NAME"
        configValue = "ABC Public School - Updated"
        category = "GENERAL"
        description = "Official school name (updated)"
        isEditable = $true
        dataType = "STRING"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/school-config/$($config1.id)" -Description "Update Config (Full)" -Body $updateConfigData
}

# Update config value only
$updateValueData = @{
    value = "45"
}
Test-Endpoint -Method "PATCH" -Url "$baseUrl/school-config/MAX_STUDENTS_PER_CLASS" -Description "Update Config Value Only" -Body $updateValueData

# ============================================================================
# DELETE OPERATIONS (Test at the end to clean up)
# ============================================================================
Write-Host "`n=== DELETE OPERATIONS ===" -ForegroundColor Yellow

# Delete a config
if ($config4 -ne $null) {
    Test-Endpoint -Method "DELETE" -Url "$baseUrl/school-config/$($config4.id)" -Description "Delete Config" -ExpectedStatusCodes @(204, 200)
}

# Delete a student (will fail if has related records)
# Commenting out to preserve test data
# if ($student3 -ne $null) {
#     Test-Endpoint -Method "DELETE" -Url "$baseUrl/students/$($student3.id)" -Description "Delete Student" -ExpectedStatusCodes @(204, 200, 400)
# }

# Delete a class (will fail if has students)
# Commenting out to preserve test data
# if ($class3 -ne $null) {
#     Test-Endpoint -Method "DELETE" -Url "$baseUrl/classes/$($class3.id)" -Description "Delete Class" -ExpectedStatusCodes @(204, 200, 400)
# }

# ============================================================================
# TEST SUMMARY
# ============================================================================
Write-Host "`n========================================" -ForegroundColor Magenta
Write-Host "TEST SUMMARY" -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta

Write-Host "`nTotal Tests: $totalTests" -ForegroundColor White
Write-Success "Passed: $passedTests"
Write-Error-Custom "Failed: $failedTests"

$passRate = if ($totalTests -gt 0) { [math]::Round(($passedTests / $totalTests) * 100, 2) } else { 0 }
Write-Host "Pass Rate: $passRate%" -ForegroundColor $(if ($passRate -ge 90) { "Green" } elseif ($passRate -ge 70) { "Yellow" } else { "Red" })

# Export results to JSON
$resultsFile = "D:\wks-autonomus\test-results.json"
$testResults | ConvertTo-Json -Depth 10 | Out-File -FilePath $resultsFile -Encoding UTF8
Write-Info "`nDetailed results saved to: $resultsFile"

# Show failed tests
if ($failedTests -gt 0) {
    Write-Host "`n=== FAILED TESTS ===" -ForegroundColor Red
    $testResults | Where-Object { $_.Status -eq "FAILED" } | Format-Table Test, Method, StatusCode -AutoSize
}

Write-Host "`n========================================" -ForegroundColor Magenta
Write-Host "Testing Complete!" -ForegroundColor Magenta
Write-Host "========================================`n" -ForegroundColor Magenta
