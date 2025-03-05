import { NextResponse } from 'next/server';

export async function GET(request: Request) {
  try {
    console.log('[API] 요청 시작: /api/resume-detail'); // 디버깅 로그 추가
    const response = await fetch('http://localhost:8080/api/v1/user/resume', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Cookie': request.headers.get('cookie') || '', 
      },
      credentials: 'include',
    });
    if (response.status === 400) {
      return NextResponse.json({ error: '이력서가 없습니다.', redirectTo: '/mypage/resume/create' }, { status: 400 });
    }
    if (!response.ok) {
      throw new Error(`[API] 요청 실패: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();
    if (!data.data) {
      return NextResponse.json({ error: '이력서가 없습니다.' }, { status: 404 });
    }
    return NextResponse.json(data);
  } catch (err) {
    return NextResponse.json({ error: err instanceof Error ? err.message : '알 수 없는 오류' }, { status: 500 });
  }
}
