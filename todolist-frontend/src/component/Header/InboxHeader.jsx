import { MenuOutlined, MessageOutlined, MoreOutlined } from "@ant-design/icons";

export default function InboxHeader() {
  return (
    <div className="flex justify-end items-center px-4 py-2  bg-white">
      <div className="flex items-center gap-3">
        <MenuOutlined className="text-gray-500 text-lg cursor-pointer hover:text-blue-500" />
        <span className="text-gray-500 font-light select-none">Display</span>
        <MessageOutlined className="text-gray-500 text-lg cursor-pointer hover:text-blue-500" />
        <MoreOutlined className="text-gray-500 text-lg cursor-pointer hover:text-blue-500" />
      </div>
    </div>
  );
}
