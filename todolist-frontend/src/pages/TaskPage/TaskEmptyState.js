import { Button, Typography } from "antd";

const { Paragraph, Title } = Typography;

export default function TaskEmptyState() {
  return (
    <div className="flex flex-col items-center justify-center mt-20">
      <img
        src="https://cdn-icons-png.flaticon.com/512/4072/4072353.png"
        alt="empty inbox"
        className="w-40 h-40"
      />
      <Title level={4} className="mt-4">
        Capture now, plan later
      </Title>
      <Paragraph className="text-center text-gray-500 max-w-sm">
        Inbox is your go-to spot for quick task entry. Clear your mind now,
        organize when you’re ready.
      </Paragraph>
      <Button type="primary" danger className="mt-4">
        + Add task
      </Button>
    </div>
  );
}
