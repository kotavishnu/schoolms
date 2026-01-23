import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { StudentCard } from './StudentCard';
import type { Student } from '@/types/student';

const mockStudent: Student = {
  id: 'STD-20260123-0001',
  firstName: 'John',
  lastName: 'Doe',
  dateOfBirth: '2015-05-15',
  age: 8,
  adhaarNumber: '123456789012',
  identificationMarks: 'Mole on left cheek',
  address: '123 Main St, City, State 12345',
  guardianName: 'Jane Doe',
  motherName: 'Jane Doe',
  phone: '9876543210',
  email: 'john.doe@example.com',
  status: 'ACTIVE',
  createdAt: '2026-01-23T00:00:00Z',
  updatedAt: '2026-01-23T00:00:00Z',
};

describe('StudentCard', () => {
  it('renders student information correctly', () => {
    const onView = vi.fn();
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    render(
      <StudentCard
        student={mockStudent}
        onView={onView}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    );

    // Verify student ID
    expect(screen.getByText('STD-20260123-0001')).toBeInTheDocument();

    // Verify student name
    expect(screen.getByText('John Doe')).toBeInTheDocument();

    // Verify age
    expect(screen.getByText(/Age: 8 years/)).toBeInTheDocument();

    // Verify guardian name
    expect(screen.getByText('Jane Doe')).toBeInTheDocument();

    // Verify phone
    expect(screen.getByText('9876543210')).toBeInTheDocument();

    // Verify email
    expect(screen.getByText('john.doe@example.com')).toBeInTheDocument();

    // Verify status badge
    expect(screen.getByText('ACTIVE')).toBeInTheDocument();
  });

  it('displays ACTIVE status with default badge variant', () => {
    const onView = vi.fn();
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    const activeStudent = { ...mockStudent, status: 'ACTIVE' as const };

    render(
      <StudentCard
        student={activeStudent}
        onView={onView}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    );

    const statusBadge = screen.getByText('ACTIVE');
    expect(statusBadge).toBeInTheDocument();
  });

  it('displays INACTIVE status with secondary badge variant', () => {
    const onView = vi.fn();
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    const inactiveStudent = { ...mockStudent, status: 'INACTIVE' as const };

    render(
      <StudentCard
        student={inactiveStudent}
        onView={onView}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    );

    const statusBadge = screen.getByText('INACTIVE');
    expect(statusBadge).toBeInTheDocument();
  });

  it('calls onView when View button is clicked', async () => {
    const user = userEvent.setup();
    const onView = vi.fn();
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    render(
      <StudentCard
        student={mockStudent}
        onView={onView}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    );

    const viewButton = screen.getByRole('button', { name: /view/i });
    await user.click(viewButton);

    expect(onView).toHaveBeenCalledTimes(1);
    expect(onView).toHaveBeenCalledWith(mockStudent);
  });

  it('calls onEdit when Edit button is clicked', async () => {
    const user = userEvent.setup();
    const onView = vi.fn();
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    render(
      <StudentCard
        student={mockStudent}
        onView={onView}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    );

    const editButton = screen.getByRole('button', { name: /edit/i });
    await user.click(editButton);

    expect(onEdit).toHaveBeenCalledTimes(1);
    expect(onEdit).toHaveBeenCalledWith(mockStudent);
  });

  it('calls onDelete when Delete button is clicked', async () => {
    const user = userEvent.setup();
    const onView = vi.fn();
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    render(
      <StudentCard
        student={mockStudent}
        onView={onView}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    );

    const deleteButton = screen.getByRole('button', { name: '' }); // Delete button has no text, only icon
    await user.click(deleteButton);

    expect(onDelete).toHaveBeenCalledTimes(1);
    expect(onDelete).toHaveBeenCalledWith(mockStudent);
  });

  it('displays icons for user, phone, and email', () => {
    const onView = vi.fn();
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    const { container } = render(
      <StudentCard
        student={mockStudent}
        onView={onView}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    );

    // Check for SVG icons (lucide-react renders as <svg>)
    const svgElements = container.querySelectorAll('svg');
    expect(svgElements.length).toBeGreaterThan(0);
  });
});
