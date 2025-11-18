export interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail: string;
  timestamp: string;
  errors?: string[];
}

export interface ApiError {
  problemDetail: ProblemDetail;
  statusCode: number;
}
