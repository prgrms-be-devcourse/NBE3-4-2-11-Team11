// /utils/api.ts
import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import { getAccessToken, getRefreshToken, setTokens, removeTokens } from './token';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: API_URL,
});

// 요청 인터셉터: Access Token 첨부
api.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const token = getAccessToken();
    console.log('[Request Interceptor] Outgoing request to:', config.url);
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`;
      console.log('[Request Interceptor] Added Authorization header with token:', token);
    } else {
      console.log('[Request Interceptor] No access token found.');
    }
    return config;
  },
  (error) => {
    console.error('[Request Interceptor] Error in request:', error);
    return Promise.reject(error);
  }
);

// 응답 인터셉터: 401 또는 403 에러 발생 시 Refresh Token을 통한 토큰 갱신 시도
api.interceptors.response.use(
  (response) => {
    console.log('[Response Interceptor] Received response for:', response.config.url, 'Status:', response.status);
    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config;
    console.error('[Response Interceptor] Error response for:', originalRequest.url, 'Status:', error.response?.status);

    // 만약 서버가 401 Unauthorized 또는 403 Forbidden을 반환하면 Refresh Token 갱신 시도
    if ((error.response?.status === 401 || error.response?.status === 403) && !originalRequest._retry) {
      originalRequest._retry = true;
      console.log('[Response Interceptor] Detected', error.response?.status, 'error. Attempting token refresh...');
      const refreshToken = getRefreshToken();
      if (refreshToken) {
        console.log('[Response Interceptor] Found refresh token:', refreshToken);
        try {
          const refreshResponse = await axios.post(
            `${API_URL}/admin/refresh-token`,
            null,
            { headers: { 'Refresh-Token': refreshToken } }
          );
          console.log('[Response Interceptor] Refresh token request successful. Response:', refreshResponse.data);
          const { accessToken, refreshToken: newRefreshToken } = refreshResponse.data.data;
          console.log('[Response Interceptor] New access token:', accessToken);
          console.log('[Response Interceptor] New refresh token:', newRefreshToken);
          setTokens(accessToken, newRefreshToken);
          originalRequest.headers = originalRequest.headers || {};
          originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;
          console.log('[Response Interceptor] Retrying original request with new access token.');
          return axios(originalRequest);
        } catch (refreshError) {
          console.error('[Response Interceptor] Error refreshing token:', refreshError);
          removeTokens();
          if (typeof window !== 'undefined') {
            console.log('[Response Interceptor] Redirecting to login page.');
            window.location.href = '/admin/login';
          }
          return Promise.reject(refreshError);
        }
      } else {
        console.error('[Response Interceptor] No refresh token available.');
        removeTokens();
        if (typeof window !== 'undefined') {
          console.log('[Response Interceptor] Redirecting to login page.');
          window.location.href = '/admin/login';
        }
      }
    }
    return Promise.reject(error);
  }
);

export default api;
