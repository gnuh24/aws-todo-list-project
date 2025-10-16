import { Typography } from "antd";

const { Title } = Typography;

export default function TaskHeader() {
  return (
    <div className="p-6">
      <Title level={2}>Inbox</Title>
    </div>
  );
}
