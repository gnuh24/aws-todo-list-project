import { Button } from "antd";
import { InboxOutlined } from "@ant-design/icons";

export default function HeaderBar() {
  return (
    <div className="flex items-center justify-between px-8 py-5 border-b bg-white">
      <div className="flex items-center gap-2">
        <InboxOutlined className="text-red-500 text-lg" />
        <h2 className="text-xl font-semibold">Inbox</h2>
      </div>

      <div className="flex items-center gap-4 text-gray-600">
        <button className="hover:text-black text-sm">Display</button>
        <button className="hover:text-black text-sm">...</button>
      </div>
    </div>
  );
}
