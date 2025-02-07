// 공통적인 api 요청 fetch에서 관리

import axios from 'axios';

//기본 url 설정 
const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1',
  headers: { 'Content-Type': 'application/json' },
});


// 응답 인터셉터 (공통 에러 처리) -> 특정api요청에서 별도의 추가 처리 필요하면 각 함수에 try-catch추가
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response) {
        const message = error.response.data.message || '알 수 없는 오류가 발생했습니다.';
        alert(`에러: ${message}`);
      } else {
        alert('서버와 연결할 수 없습니다.');
      }
      return Promise.reject(error);
    }
  );

export default apiClient;