// 공통적인 api 요청 fetch에서 관리

import axios from 'axios';


//기본 url 설정 
const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1',
  headers: { 'Content-Type': 'application/json' },
});


export default apiClient;