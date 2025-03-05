// frontend/app/components/DormantAccountsChart.tsx
"use client";

import { Pie } from "react-chartjs-2";
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(ArcElement, Tooltip, Legend);

interface DormantAccountsChartProps {
  userStats: { dormantFlg?: string }[];
}

export default function DormantAccountsChart({ userStats }: DormantAccountsChartProps) {
  const activeCount = userStats.filter(user => user.dormantFlg !== "Y").length;
  const dormantCount = userStats.filter(user => user.dormantFlg === "Y").length;

  const data = {
    labels: ["활성 계정", "휴먼 계정"],
    datasets: [
      {
        data: [activeCount, dormantCount],
        backgroundColor: ["rgba(75, 192, 192, 0.6)", "rgba(255, 99, 132, 0.6)"],
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: "top" as const },
      title: { display: true, text: "계정 상태 (활성 vs 휴먼)", font: { size: 16 } },
    },
  };

  return (
    <div style={{ width: "300px", height: "300px", margin: "0 auto" }}>
      <Pie data={data} options={options} />
    </div>
  );
}
