import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { Skeleton } from './skeleton';

describe('Skeleton Component', () => {
  it('should render without crashing', () => {
    const { container } = render(<Skeleton />);
    expect(container.firstChild).toBeInTheDocument();
  });

  it('should apply animate-pulse class', () => {
    const { container } = render(<Skeleton />);
    const element = container.firstChild as HTMLElement;
    expect(element).toHaveClass('animate-pulse');
  });

  it('should apply rounded-md class', () => {
    const { container } = render(<Skeleton />);
    const element = container.firstChild as HTMLElement;
    expect(element).toHaveClass('rounded-md');
  });

  it('should apply custom className', () => {
    const { container } = render(<Skeleton className="custom-class" />);
    const element = container.firstChild as HTMLElement;
    expect(element).toHaveClass('custom-class');
  });

  it('should apply custom width and height', () => {
    const { container } = render(<Skeleton className="w-24 h-8" />);
    const element = container.firstChild as HTMLElement;
    expect(element).toHaveClass('w-24');
    expect(element).toHaveClass('h-8');
  });
});
