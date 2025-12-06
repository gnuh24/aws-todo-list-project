import {
  DownOutlined,
  InboxOutlined,
  FolderOutlined,
  ProjectOutlined,
  NumberOutlined,
  SmileOutlined,
  TeamOutlined,
  UnorderedListOutlined,
  PlusCircleFilled,
  EditFilled,
  CheckCircleFilled,
  CloseCircleFilled,
  DeleteFilled,
  MessageFilled,
  StopFilled,
  SearchOutlined,
  UserOutlined,
  CheckOutlined,
} from "@ant-design/icons";
import { Dropdown, Input, Typography, Divider } from "antd";
import { useState } from "react";

const { Title } = Typography;
const { Text } = Typography;
export default function ActivityHeader() {
  const [selectedProject, setSelectedProject] = useState("All Projects");
  const [selectedFilter, setSelectedFilter] = useState("Completed tasks");
  const [selectedCollaborator, setSelectedCollaborator] = useState("Everyone");

  const projectMenu = (
    <div className="w-60 p-2 bg-white shadow-lg">
      {/* Ô tìm kiếm */}
      <Input
        size="small"
        placeholder="Type a project name"
        className="mb-2 text-sm"
      />

      {/* Danh sách dự án */}
      <div className="space-y-2 text-sm text-gray-700">
        <div
          className="flex items-center gap-2 px-2 py-1 rounded cursor-pointer hover:bg-gray-100"
          onClick={() => setSelectedProject("All Projects")}
        >
          <NumberOutlined /> All Projects
        </div>

        <div
          className="flex items-center gap-2 px-2 py-1 rounded cursor-pointer hover:bg-gray-100"
          onClick={() => setSelectedProject("Inbox")}
        >
          <InboxOutlined /> Inbox
        </div>

        <Divider className="my-2" />

        <Text type="secondary" className="pl-2 text-xs uppercase">
          My Projects
        </Text>

        <div
          className="flex items-center gap-2 px-2 py-1 rounded cursor-pointer hover:bg-gray-100"
          onClick={() => setSelectedProject("Getting Started 👋")}
        >
          <ProjectOutlined /> Getting Started 👋
        </div>
      </div>
    </div>
  );

  // --- Collaborator Dropdown ---
  const collaborators = [
    { name: "Everyone", icon: <UserOutlined /> },
    { name: "Duong Binh Minh", icon: <UserOutlined /> },
    { name: "Hoang An", icon: <UserOutlined /> },
    { name: "Linh Tran", icon: <UserOutlined /> },
  ];

  const collaboratorMenu = (
    <div className="bg-white border rounded-md shadow-lg w-56 p-2">
      <Input
        size="small"
        prefix={<SearchOutlined />}
        placeholder="Filter by collaborator"
        className="mb-2"
      />
      <ul>
        {collaborators.map((user) => (
          <li
            key={user.name}
            onClick={() => setSelectedCollaborator(user.name)}
            className={`flex items-center justify-between px-3 py-1.5 text-[13px] rounded cursor-pointer hover:bg-gray-100 ${
              selectedCollaborator === user.name ? "bg-gray-50" : ""
            }`}
          >
            <div className="flex items-center gap-2">
              {user.icon}
              <span>{user.name}</span>
            </div>
            {selectedCollaborator === user.name && (
              <CheckOutlined className="text-red-500 text-xs" />
            )}
          </li>
        ))}
      </ul>
    </div>
  );

  // --- Activity Filter Dropdown ---
  const filterItems = [
    {
      label: "All actions",
      icon: <UnorderedListOutlined className="text-gray-500" />,
    },
    {
      label: "Added tasks",
      icon: <PlusCircleFilled className="text-green-500" />,
    },
    { label: "Updated tasks", icon: <EditFilled className="text-blue-500" /> },
    {
      label: "Completed tasks",
      icon: <CheckCircleFilled className="text-green-500" />,
    },
    {
      label: "Uncompleted tasks",
      icon: <CloseCircleFilled className="text-gray-400" />,
    },
    { label: "Deleted tasks", icon: <DeleteFilled className="text-red-500" /> },
    {
      label: "Added comments",
      icon: <MessageFilled className="text-green-600" />,
    },
    {
      label: "Deleted comments",
      icon: <StopFilled className="text-red-400" />,
    },
  ];

  const filterMenu = (
    <div className="bg-white border rounded-md shadow-lg w-56 p-2">
      <Input
        size="small"
        prefix={<SearchOutlined />}
        placeholder="Filter by activity"
        className="mb-2"
      />
      <ul className="max-h-64 overflow-y-auto">
        {filterItems.map((item) => (
          <li
            key={item.label}
            onClick={() => setSelectedFilter(item.label)}
            className={`flex items-center gap-2 px-3 py-1.5 rounded cursor-pointer text-[13px] hover:bg-gray-100 ${
              selectedFilter === item.label ? "bg-gray-50 font-medium" : ""
            }`}
          >
            {item.icon}
            <span>{item.label}</span>
            {selectedFilter === item.label && (
              <CheckCircleFilled className="ml-auto text-green-500 text-xs" />
            )}
          </li>
        ))}
      </ul>
    </div>
  );

  return (
    <div className="flex items-center justify-between mb-8">
      {/* Left: Title */}
      <div className="flex items-center gap-2">
        <h3 className="text-xl font-semibold">
          Activity:{" "}
          <Dropdown overlay={projectMenu} trigger={["click"]}>
            <span className="font-normal cursor-pointer hover:underline select-none">
              {selectedProject} <DownOutlined className="text-xs" />
            </span>
          </Dropdown>
        </h3>
      </div>

      {/* Right: Filters (ví dụ giữ nguyên như bạn có) */}
      <div className="flex items-center gap-5 text-gray-600">
        <div className="flex items-center gap-1 cursor-pointer hover:text-black">
          <FolderOutlined /> Collaborators
        </div>

        <div className="flex items-center gap-1 cursor-pointer hover:text-black">
          <SmileOutlined /> Filters
        </div>
      </div>
    </div>
  );
}
