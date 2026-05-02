export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface OtpVerifyRequest {
  email: string;
  otpCode: string;
}

export interface LoginResponse {
  token: string;
  publicId: string;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export interface AuthUser {
  token: string;
  publicId: string;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN';
}
