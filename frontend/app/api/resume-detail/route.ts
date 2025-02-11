import { NextResponse } from 'next/server';

export async function GET(request: Request) {
  try {
    console.log('[API] 요청 시작: /api/resume-detail'); // 디버깅 로그 추가
    const accessToken = request.headers.get('Authorization')?.replace('Bearer ', '');
    const response = await fetch('http://localhost:8080/api/v1/user/resume', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${accessToken}`, 
        'Content-Type': 'application/json',
      },
    });
    if (response.status === 400) {
      console.log('[API] 이력서가 없습니다. 생성 페이지로 리디렉션합니다.');
      return NextResponse.json({ error: '이력서가 없습니다.', redirectTo: '/mypage/resume/create' }, { status: 400 });
    }
    if (!response.ok) {
        throw new Error(`[API] 요청 실패: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();
    if (!data.data) {
      return NextResponse.json({ error: '이력서가 없습니다.' }, { status: 404 });
    }
    console.log('[API] 응답 데이터:', data); // 응답 데이터 확인용 로그

    return NextResponse.json(data);
  } catch (err) {
    console.error('[API] 에러 발생:', err); // 콘솔에 에러 메시지 출력
    return NextResponse.json({ error: err instanceof Error ? err.message : '알 수 없는 오류' }, { status: 500 });
  }
}
