// /utils/api.ts
import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import { getAccessToken, getRefreshToken, setTokens, removeTokens } from './token';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: API_URL,
});

// 요청 인터셉터: localStorage에 저장된 Access Token을 헤더에 자동 첨부
api.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const token = getAccessToken();
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터: 401 또는 403 에러 발생 시 Refresh Token으로 토큰 갱신 후 원래 요청 재시도
api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config;
    if (
      (error.response?.status === 401 || error.response?.status === 403) &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;
      const refreshToken = getRefreshToken();
      if (refreshToken) {
        try {
          const refreshResponse = await axios.post(
            `${API_URL}/admin/refresh-token`,
            null,
            { headers: { 'Refresh-Token': refreshToken } }
          );
          const { accessToken, refreshToken: newRefreshToken } = refreshResponse.data.data;
          setTokens(accessToken, newRefreshToken);
          originalRequest.headers = originalRequest.headers || {};
          originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;
          return axios(originalRequest);
        } catch (refreshError) {
          removeTokens();
          if (typeof window !== 'undefined') {
            window.location.href = '/admin/login';
          }
          return Promise.reject(refreshError);
        }
      } else {
        removeTokens();
        if (typeof window !== 'undefined') {
          window.location.href = '/admin/login';
        }
      }
    }
    return Promise.reject(error);
  }
);

export default api;
