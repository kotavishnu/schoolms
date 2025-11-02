# CORRECTED Comprehensive API Testing Script for School Management System
# Tests all 65 endpoints across 6 controllers with proper validation

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
        $statusCode = if ($_.Exception.Response) { $_.Exception.Response.StatusCode.value__ } else { "N/A" }
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

# Get all existing classes first
$existingClasses = Test-Endpoint -Method "GET" -Url "$baseUrl/classes" -Description "Get All Classes"

# Get classes by academic year
Test-Endpoint -Method "GET" -Url "$baseUrl/classes?academicYear=2024-2025" -Description "Get Classes by Academic Year"

# Get class by ID (use existing class)
if ($existingClasses.data -and $existingClasses.data.Count -gt 0) {
    $testClassId = $existingClasses.data[0].id
    Test-Endpoint -Method "GET" -Url "$baseUrl/classes/$testClassId" -Description "Get Class by ID"
}

# Create new class for 2025-2026
$newClassData = @{
    classNumber = 5
    section = "B"
    academicYear = "2025-2026"
    capacity = 40
    classTeacher = "Mrs. Johnson"
    roomNumber = "505"
}
$newClass = Test-Endpoint -Method "POST" -Url "$baseUrl/classes" -Description "Create New Class 5-B" -Body $newClassData -ExpectedStatusCodes @(201)

# Get classes by class number
Test-Endpoint -Method "GET" -Url "$baseUrl/classes/by-number?classNumber=1&academicYear=2024-2025" -Description "Get Classes by Class Number"

# Get classes with available seats
Test-Endpoint -Method "GET" -Url "$baseUrl/classes/available?academicYear=2024-2025" -Description "Get Classes with Available Seats"

# Get almost full classes
Test-Endpoint -Method "GET" -Url "$baseUrl/classes/almost-full?academicYear=2024-2025" -Description "Get Almost Full Classes"

# Get total students
Test-Endpoint -Method "GET" -Url "$baseUrl/classes/total-students?academicYear=2024-2025" -Description "Get Total Students"

# Check if class exists
Test-Endpoint -Method "GET" -Url "$baseUrl/classes/exists?classNumber=1&section=A&academicYear=2024-2025" -Description "Check if Class Exists"

# Update class
if ($newClass -and $newClass.data) {
    $updateData = @{
        classNumber = 5
        section = "B"
        academicYear = "2025-2026"
        capacity = 45
        classTeacher = "Mrs. Johnson-Updated"
        roomNumber = "505"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/classes/$($newClass.data.id)" -Description "Update Class" -Body $updateData
}

# ============================================================================
# 2. STUDENT CONTROLLER TESTS (8 endpoints)
# ============================================================================
Write-Host "`n=== STUDENT CONTROLLER TESTS ===" -ForegroundColor Yellow

# Use existing class ID (Class 1-A from 2024-2025)
$class1Id = ($existingClasses.data | Where-Object { $_.classNumber -eq 1 -and $_.section -eq "A" -and $_.academicYear -eq "2024-2025" }).id

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
    classId = $class1Id
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
    classId = $class1Id
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
    classId = $class1Id
    enrollmentDate = "2024-04-01"
    status = "ACTIVE"
}
$student3 = Test-Endpoint -Method "POST" -Url "$baseUrl/students" -Description "Create Student Rahul Kumar" -Body $studentData3 -ExpectedStatusCodes @(201)

# Get student by ID
if ($student1 -and $student1.data) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/students/$($student1.data.id)" -Description "Get Student by ID"
}

# Get all students
Test-Endpoint -Method "GET" -Url "$baseUrl/students" -Description "Get All Students"

# Get students by class
Test-Endpoint -Method "GET" -Url "$baseUrl/students?classId=$class1Id" -Description "Get Students by Class"

# Search students by name
Test-Endpoint -Method "GET" -Url "$baseUrl/students/search?q=John" -Description "Search Students by Name"

# Autocomplete search
Test-Endpoint -Method "GET" -Url "$baseUrl/students/autocomplete?q=98" -Description "Autocomplete Search (Mobile)"

# Get pending fees students
Test-Endpoint -Method "GET" -Url "$baseUrl/students/pending-fees" -Description "Get Students with Pending Fees"

# Update student
if ($student1 -and $student1.data) {
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
        classId = $class1Id
        enrollmentDate = "2024-04-01"
        status = "ACTIVE"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/students/$($student1.data.id)" -Description "Update Student" -Body $updateStudentData
}

# ============================================================================
# 3. FEE MASTER CONTROLLER TESTS (12 endpoints)
# ============================================================================
Write-Host "`n=== FEE MASTER CONTROLLER TESTS ===" -ForegroundColor Yellow

# Create Fee Master #1 - Tuition (use future date for applicableTo)
$feeMasterData1 = @{
    feeType = "TUITION"
    amount = 5000.00
    frequency = "MONTHLY"
    applicableFrom = "2024-11-01"
    applicableTo = "2025-12-31"
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
    applicableFrom = "2024-11-01"
    applicableTo = "2025-12-31"
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
    applicableFrom = "2024-11-01"
    applicableTo = "2025-12-31"
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
    applicableFrom = "2024-11-01"
    applicableTo = "2025-12-31"
    description = "Annual sports fee"
    isActive = $false
    academicYear = "2024-2025"
}
$feeMaster4 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-masters" -Description "Create Fee Master - Sports (Inactive)" -Body $feeMasterData4 -ExpectedStatusCodes @(201)

# Get fee master by ID
if ($feeMaster1 -and $feeMaster1.data) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-masters/$($feeMaster1.data.id)" -Description "Get Fee Master by ID"
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
if ($feeMaster1 -and $feeMaster1.data) {
    $updateFeeMasterData = @{
        feeType = "TUITION"
        amount = 5500.00
        frequency = "MONTHLY"
        applicableFrom = "2024-11-01"
        applicableTo = "2025-12-31"
        description = "Updated monthly tuition fee"
        isActive = $true
        academicYear = "2024-2025"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/fee-masters/$($feeMaster1.data.id)" -Description "Update Fee Master" -Body $updateFeeMasterData
}

# Activate fee master
if ($feeMaster4 -and $feeMaster4.data) {
    Test-Endpoint -Method "PATCH" -Url "$baseUrl/fee-masters/$($feeMaster4.data.id)/activate" -Description "Activate Fee Master"
}

# Deactivate fee master
if ($feeMaster4 -and $feeMaster4.data) {
    Test-Endpoint -Method "PATCH" -Url "$baseUrl/fee-masters/$($feeMaster4.data.id)/deactivate" -Description "Deactivate Fee Master"
}

# ============================================================================
# 4. FEE JOURNAL CONTROLLER TESTS (12 endpoints)
# ============================================================================
Write-Host "`n=== FEE JOURNAL CONTROLLER TESTS ===" -ForegroundColor Yellow

# Use month names and future dates
if ($student1 -and $student1.data) {
    $journalData1 = @{
        studentId = $student1.data.id
        month = "December"
        year = 2025
        amountDue = 5500.00
        amountPaid = 0.00
        dueDate = "2025-12-10"
        remarks = "December 2025 fee"
    }
    $journal1 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-journals" -Description "Create Fee Journal - December 2025" -Body $journalData1 -ExpectedStatusCodes @(201)
}

# Create Fee Journal #2 - Partially Paid
if ($student1 -and $student1.data) {
    $journalData2 = @{
        studentId = $student1.data.id
        month = "January"
        year = 2026
        amountDue = 5500.00
        amountPaid = 2000.00
        dueDate = "2026-01-10"
        remarks = "January 2026 fee - Partial payment"
    }
    $journal2 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-journals" -Description "Create Fee Journal - January 2026 (Partial)" -Body $journalData2 -ExpectedStatusCodes @(201)
}

# Create Fee Journal #3 - For student 2
if ($student2 -and $student2.data) {
    $journalData3 = @{
        studentId = $student2.data.id
        month = "December"
        year = 2025
        amountDue = 5500.00
        amountPaid = 0.00
        dueDate = "2025-12-15"
        remarks = "December 2025 fee"
    }
    $journal3 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-journals" -Description "Create Fee Journal - Student 2" -Body $journalData3 -ExpectedStatusCodes @(201)
}

# Create Fee Journal #4 - For student 3
if ($student3 -and $student3.data) {
    $journalData4 = @{
        studentId = $student3.data.id
        month = "February"
        year = 2026
        amountDue = 5000.00
        amountPaid = 0.00
        dueDate = "2026-02-10"
        remarks = "February 2026 fee"
    }
    $journal4 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-journals" -Description "Create Fee Journal - Student 3" -Body $journalData4 -ExpectedStatusCodes @(201)
}

# Get fee journal by ID
if ($journal1 -and $journal1.data) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/$($journal1.data.id)" -Description "Get Fee Journal by ID"
}

# Get all fee journals
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals" -Description "Get All Fee Journals"

# Get journals for student
if ($student1 -and $student1.data) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/student/$($student1.data.id)" -Description "Get Journals for Student"
}

# Get pending journals for student
if ($student1 -and $student1.data) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/student/$($student1.data.id)/pending" -Description "Get Pending Journals for Student"
}

# Get journals by month
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/by-month?month=December&year=2025" -Description "Get Journals by Month"

# Get journals by status
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/by-status/PENDING" -Description "Get Journals by Status (PENDING)"

# Get overdue journals
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/overdue" -Description "Get Overdue Journals"

# Get student dues summary
if ($student1 -and $student1.data) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-journals/student/$($student1.data.id)/summary" -Description "Get Student Dues Summary"
}

# Update fee journal
if ($journal1 -and $journal1.data -and $student1 -and $student1.data) {
    $updateJournalData = @{
        studentId = $student1.data.id
        month = "December"
        year = 2025
        amountDue = 5500.00
        amountPaid = 1000.00
        dueDate = "2025-12-10"
        remarks = "December 2025 fee - Updated with partial payment"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/fee-journals/$($journal1.data.id)" -Description "Update Fee Journal" -Body $updateJournalData
}

# Record payment
if ($journal2 -and $journal2.data) {
    $paymentData = @{
        amount = 3500.00
    }
    Test-Endpoint -Method "PATCH" -Url "$baseUrl/fee-journals/$($journal2.data.id)/payment" -Description "Record Payment" -Body $paymentData
}

# ============================================================================
# 5. FEE RECEIPT CONTROLLER TESTS (13 endpoints)
# ============================================================================
Write-Host "`n=== FEE RECEIPT CONTROLLER TESTS ===" -ForegroundColor Yellow

# Create Fee Receipt #1 - Cash Payment
if ($student1 -and $student1.data) {
    $receiptData1 = @{
        studentId = $student1.data.id
        amount = 5500.00
        paymentDate = "2025-11-02"
        paymentMethod = "CASH"
        monthsPaid = @("December")
        feeBreakdown = @{
            TUITION = 5000.00
            LIBRARY = 500.00
        }
        remarks = "Cash payment for December 2025"
        generatedBy = "Admin"
    }
    $receipt1 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-receipts" -Description "Generate Fee Receipt - Cash" -Body $receiptData1 -ExpectedStatusCodes @(201)
}

# Create Fee Receipt #2 - Online Payment
if ($student2 -and $student2.data) {
    $receiptData2 = @{
        studentId = $student2.data.id
        amount = 6000.00
        paymentDate = "2025-11-02"
        paymentMethod = "ONLINE"
        transactionId = "TXN123456789"
        monthsPaid = @("December", "January")
        feeBreakdown = @{
            TUITION = 5500.00
            LIBRARY = 500.00
        }
        remarks = "Online payment for Dec-Jan"
        generatedBy = "Admin"
    }
    $receipt2 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-receipts" -Description "Generate Fee Receipt - Online" -Body $receiptData2 -ExpectedStatusCodes @(201)
}

# Create Fee Receipt #3 - Cheque Payment
if ($student3 -and $student3.data) {
    $receiptData3 = @{
        studentId = $student3.data.id
        amount = 5500.00
        paymentDate = "2025-11-02"
        paymentMethod = "CHEQUE"
        chequeNumber = "CHQ789456"
        bankName = "State Bank"
        monthsPaid = @("February")
        feeBreakdown = @{
            TUITION = 5000.00
            COMPUTER = 500.00
        }
        remarks = "Cheque payment for February 2026"
        generatedBy = "Admin"
    }
    $receipt3 = Test-Endpoint -Method "POST" -Url "$baseUrl/fee-receipts" -Description "Generate Fee Receipt - Cheque" -Body $receiptData3 -ExpectedStatusCodes @(201)
}

# Get receipt by ID
if ($receipt1 -and $receipt1.data) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/$($receipt1.data.id)" -Description "Get Fee Receipt by ID"
}

# Get receipt by receipt number
if ($receipt1 -and $receipt1.data -and $receipt1.data.receiptNumber) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/number/$($receipt1.data.receiptNumber)" -Description "Get Receipt by Receipt Number"
}

# Get all receipts
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts" -Description "Get All Fee Receipts"

# Get receipts for student
if ($student1 -and $student1.data) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/student/$($student1.data.id)" -Description "Get Receipts for Student"
}

# Get receipts by date range
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/by-date?startDate=2025-11-01&endDate=2025-11-03" -Description "Get Receipts by Date Range"

# Get receipts by payment method
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/by-method/CASH" -Description "Get Receipts by Payment Method"

# Get today's receipts
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/today" -Description "Get Today's Receipts"

# Get total collection for date range
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/collection?startDate=2025-11-01&endDate=2025-11-03" -Description "Get Total Collection"

# Get collection by payment method
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/collection/by-method?paymentMethod=CASH&startDate=2025-11-01&endDate=2025-11-03" -Description "Get Collection by Payment Method"

# Get collection summary
Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/collection/summary?startDate=2025-11-01&endDate=2025-11-03" -Description "Get Collection Summary"

# Count receipts for student
if ($student1 -and $student1.data) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/fee-receipts/count/$($student1.data.id)" -Description "Count Receipts for Student"
}

# ============================================================================
# 6. SCHOOL CONFIG CONTROLLER TESTS (10 endpoints)
# ============================================================================
Write-Host "`n=== SCHOOL CONFIG CONTROLLER TESTS ===" -ForegroundColor Yellow

# Create Config #1
$configData1 = @{
    configKey = "SCHOOL_NAME_TEST"
    configValue = "ABC Public School"
    category = "GENERAL"
    description = "Official school name"
    isEditable = $true
    dataType = "STRING"
}
$config1 = Test-Endpoint -Method "POST" -Url "$baseUrl/school-config" -Description "Create Config - School Name" -Body $configData1 -ExpectedStatusCodes @(201)

# Create Config #2
$configData2 = @{
    configKey = "MAX_STUDENTS_TEST"
    configValue = "50"
    category = "ACADEMIC"
    description = "Maximum students allowed per class"
    isEditable = $true
    dataType = "INTEGER"
}
$config2 = Test-Endpoint -Method "POST" -Url "$baseUrl/school-config" -Description "Create Config - Max Students" -Body $configData2 -ExpectedStatusCodes @(201)

# Create Config #3
$configData3 = @{
    configKey = "ENABLE_SMS_TEST"
    configValue = "true"
    category = "NOTIFICATION"
    description = "Enable SMS notifications"
    isEditable = $true
    dataType = "BOOLEAN"
}
$config3 = Test-Endpoint -Method "POST" -Url "$baseUrl/school-config" -Description "Create Config - SMS Notifications" -Body $configData3 -ExpectedStatusCodes @(201)

# Create Config #4 - System Config
$configData4 = @{
    configKey = "SYSTEM_VERSION_TEST"
    configValue = "1.0.0"
    category = "SYSTEM"
    description = "System version number"
    isEditable = $false
    dataType = "STRING"
}
$config4 = Test-Endpoint -Method "POST" -Url "$baseUrl/school-config" -Description "Create Config - System Version (Not Editable)" -Body $configData4 -ExpectedStatusCodes @(201)

# Get config by ID
if ($config1 -and $config1.data) {
    Test-Endpoint -Method "GET" -Url "$baseUrl/school-config/$($config1.data.id)" -Description "Get Config by ID"
}

# Get config by key
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config/key/SCHOOL_NAME_TEST" -Description "Get Config by Key"

# Get config value only
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config/value/SCHOOL_NAME_TEST" -Description "Get Config Value Only"

# Get all configs
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config" -Description "Get All Configs"

# Get configs by category
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config?category=ACADEMIC" -Description "Get Configs by Category"

# Get editable configs only
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config/editable" -Description "Get Editable Configs Only"

# Check if config exists
Test-Endpoint -Method "GET" -Url "$baseUrl/school-config/exists/SCHOOL_NAME_TEST" -Description "Check if Config Exists"

# Update config (full update)
if ($config1 -and $config1.data) {
    $updateConfigData = @{
        configKey = "SCHOOL_NAME_TEST"
        configValue = "ABC Public School - Updated"
        category = "GENERAL"
        description = "Official school name (updated)"
        isEditable = $true
        dataType = "STRING"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/school-config/$($config1.data.id)" -Description "Update Config (Full)" -Body $updateConfigData
}

# Update config value only
$updateValueData = @{
    value = "45"
}
Test-Endpoint -Method "PATCH" -Url "$baseUrl/school-config/MAX_STUDENTS_TEST" -Description "Update Config Value Only" -Body $updateValueData

# ============================================================================
# DELETE OPERATIONS (Test at the end to clean up)
# ============================================================================
Write-Host "`n=== DELETE OPERATIONS ===" -ForegroundColor Yellow

# Delete a config
if ($config4 -and $config4.data) {
    Test-Endpoint -Method "DELETE" -Url "$baseUrl/school-config/$($config4.data.id)" -Description "Delete Config" -ExpectedStatusCodes @(204, 200)
}

# Delete fee master (testing)
if ($feeMaster4 -and $feeMaster4.data) {
    Test-Endpoint -Method "DELETE" -Url "$baseUrl/fee-masters/$($feeMaster4.data.id)" -Description "Delete Fee Master" -ExpectedStatusCodes @(204, 200)
}

# Delete fee journal
if ($journal4 -and $journal4.data) {
    Test-Endpoint -Method "DELETE" -Url "$baseUrl/fee-journals/$($journal4.data.id)" -Description "Delete Fee Journal" -ExpectedStatusCodes @(204, 200)
}

# Delete class (will be tested but may fail if has students)
if ($newClass -and $newClass.data) {
    Test-Endpoint -Method "DELETE" -Url "$baseUrl/classes/$($newClass.data.id)" -Description "Delete Class (May Fail if Has Students)" -ExpectedStatusCodes @(204, 200, 400)
}

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
$resultsFile = "D:\wks-autonomus\test-results-corrected.json"
$testResults | ConvertTo-Json -Depth 10 | Out-File -FilePath $resultsFile -Encoding UTF8
Write-Info "`nDetailed results saved to: $resultsFile"

# Show failed tests
if ($failedTests -gt 0) {
    Write-Host "`n=== FAILED TESTS ===" -ForegroundColor Red
    $testResults | Where-Object { $_.Status -eq "FAILED" } | Format-Table Test, Method, StatusCode -AutoSize
}

# Show test breakdown by controller
Write-Host "`n=== TEST BREAKDOWN BY CONTROLLER ===" -ForegroundColor Cyan
$controllerStats = $testResults | Group-Object {
    if ($_.Endpoint -match "/classes") { "Class Controller" }
    elseif ($_.Endpoint -match "/students") { "Student Controller" }
    elseif ($_.Endpoint -match "/fee-masters") { "FeeMaster Controller" }
    elseif ($_.Endpoint -match "/fee-journals") { "FeeJournal Controller" }
    elseif ($_.Endpoint -match "/fee-receipts") { "FeeReceipt Controller" }
    elseif ($_.Endpoint -match "/school-config") { "SchoolConfig Controller" }
    else { "Other" }
} | ForEach-Object {
    $passed = ($_.Group | Where-Object { $_.Status -eq "PASSED" }).Count
    $failed = ($_.Group | Where-Object { $_.Status -eq "FAILED" }).Count
    [PSCustomObject]@{
        Controller = $_.Name
        Total = $_.Count
        Passed = $passed
        Failed = $failed
        PassRate = if ($_.Count -gt 0) { [math]::Round(($passed / $_.Count) * 100, 2) } else { 0 }
    }
}

$controllerStats | Format-Table -AutoSize

Write-Host "`n========================================" -ForegroundColor Magenta
Write-Host "Testing Complete!" -ForegroundColor Magenta
Write-Host "========================================`n" -ForegroundColor Magenta
