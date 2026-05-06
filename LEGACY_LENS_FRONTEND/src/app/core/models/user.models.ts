export type AccountStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';

export interface UserResponse {
  publicId: string;
  name: string;
  email: string;
  status: AccountStatus;
  verified: boolean;       // Jackson serializes boolean isVerified → "verified"
  isVerified?: boolean;    // fallback in case backend is fixed later
  createdAt: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}