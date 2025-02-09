// // /utils/api.ts
// import axios, { AxiosError, AxiosRequestConfig } from 'axios';
// import { getAccessToken, getRefreshToken, setTokens, removeTokens } from './token';
//
// const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';
//
// const api = axios.create({
//   baseURL: API_URL,
// });
//
// // 요청 인터셉터: localStorage에 저장된 Access Token을 헤더에 자동 첨부
// api.interceptors.request.use(
//   (config: AxiosRequestConfig) => {
//     const token = getAccessToken();
//     if (token && config.headers) {
//       config.headers['Authorization'] = `Bearer ${token}`;
//     }
//     return config;
//   },
//   (error) => Promise.reject(error)
// );
//
// api.interceptors.response.use(
//   (response) => response,
//   async (error: AxiosError) => {
//     console.error("🔥 Interceptor 오류 감지:", error); // ✅ 추가
//     console.error("🔥 응답 상태 코드:", error.response?.status); // ✅ 추가
//
//     const originalRequest = error.config;
//     if (
//       (error.response?.status === 401 || error.response?.status === 403) &&
//       !originalRequest._retry
//     ) {
//       console.warn("🚨 Access Token 만료됨. Refresh Token으로 재요청 시도"); // ✅ 추가
//       originalRequest._retry = true;
//       const refreshToken = getRefreshToken();
//       if (refreshToken) {
//         try {
//           console.info("🔄 Refresh Token 요청 중..."); // ✅ 추가
//           const refreshResponse = await axios.post(
//             `${API_URL}/admin/refresh-token`,
//             null,
//             { headers: { "Refresh-Token": refreshToken } }
//           );
//
//
//           console.info("✅ Refresh Token 재발급 완료", refreshResponse.data); // ✅ 추가
//
//           const { accessToken, refreshToken: newRefreshToken } = refreshResponse.data.data;
//           setTokens(accessToken, newRefreshToken);
//           originalRequest.headers = originalRequest.headers || {};
//           originalRequest.headers["Authorization"] = `Bearer ${accessToken}`;
//           return axios(originalRequest);
//         } catch (refreshError) {
//           console.error("❌ Refresh Token 요청 실패:", refreshError); // ✅ 추가
//           removeTokens();
//           if (typeof window !== "undefined") {
//             window.location.href = "/admin/login";
//           }
//           return Promise.reject(refreshError);
//         }
//       } else {
//         console.warn("🚨 Refresh Token 없음, 로그인 페이지로 이동"); // ✅ 추가
//         removeTokens();
//         if (typeof window !== "undefined") {
//           window.location.href = "/admin/login";
//         }
//       }
//     }
//     return Promise.reject(error);
//   }
// );
//
// export default api;

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
    console.error("🔥 Interceptor 오류 감지:", error);
    console.error("🔥 응답 상태 코드:", error.response?.status);

    const originalRequest = error.config;
    if (
      (error.response?.status === 401 || error.response?.status === 403) &&
      !originalRequest._retry
    ) {
      console.warn("🚨 Access Token 만료됨. Refresh Token으로 재요청 시도");
      originalRequest._retry = true;

      const refreshToken = getRefreshToken();
      console.info("🔄 현재 저장된 Refresh Token:", refreshToken);

      if (refreshToken) {
        try {
          // ✅ user와 admin의 refresh-token 경로 구분
          const isAdmin = originalRequest.url?.startsWith("/admin");
          const refreshUrl = isAdmin
            ? `${API_URL}/admin/refresh-token`
            : `${API_URL}/user/refresh-token`;

          console.info(`🔄 ${isAdmin ? "관리자" : "사용자"} Refresh Token 요청 중...`);

          const refreshResponse = await axios.post(
            refreshUrl,
            null,
            { headers: { "Refresh-Token": refreshToken } }
          );

          console.info("✅ Refresh Token 재발급 완료", refreshResponse.data);

          const { accessToken, refreshToken: newRefreshToken } = refreshResponse.data.data;
          setTokens(accessToken, newRefreshToken);

          originalRequest.headers = originalRequest.headers || {};
          originalRequest.headers["Authorization"] = `Bearer ${accessToken}`;

          return axios(originalRequest);
        } catch (refreshError) {
          console.error("❌ Refresh Token 요청 실패:", refreshError);
          removeTokens();

          if (typeof window !== "undefined") {
            console.warn("🚨 Refresh Token 만료됨. 로그인 페이지로 이동");
            window.location.href = isAdmin ? "/admin/login" : "/login";
          }

          return Promise.reject(refreshError);
        }
      } else {
        console.warn("🚨 Refresh Token 없음, 로그인 페이지로 이동");
        removeTokens();
        if (typeof window !== "undefined") {
          window.location.href = "/login";
        }
      }
    }
    return Promise.reject(error);
  }
);

export default api;
