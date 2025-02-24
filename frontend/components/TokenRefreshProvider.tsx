// frontend/components/TokenRefreshProvider.tsx
"use client";  // 이 컴포넌트는 클라이언트 전용입니다.

import useTokenRefresh from "@/utils/useTokenRefresh";

export default function TokenRefreshProvider() {
  // useTokenRefresh 훅을 호출하여 토큰 갱신 로직을 실행합니다.
  useTokenRefresh();
  return null; // UI를 렌더링하지 않음. 단순히 훅의 사이드이펙트를 위해 존재.
}
