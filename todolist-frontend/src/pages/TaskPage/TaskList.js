import {
  List,
  Checkbox,
  Tag,
  Tooltip,
  Button,
  Typography,
  Divider,
} from "antd";
import { EditOutlined, CommentOutlined, MoreOutlined } from "@ant-design/icons";
import dayjs from "dayjs";

const { Text } = Typography;

export default function TaskList({ tasks, onToggle, onEdit, onDelete }) {
  // chia nhóm task theo trạng thái Overdue / Today
  const overdue = tasks.filter(
    (t) => t.deadline && dayjs(t.deadline).isBefore(dayjs(), "day")
  );
  const today = tasks.filter(
    (t) => t.deadline && dayjs(t.deadline).isSame(dayjs(), "day")
  );

  const renderTask = (task) => (
    <List.Item
      key={task.id}
      className="hover:bg-gray-50 rounded-md px-2"
      actions={[
        <Tooltip title="Edit" key="edit">
          <Button
            type="text"
            size="small"
            icon={<EditOutlined />}
            onClick={() => onEdit(task.id)}
          />
        </Tooltip>,
        <Tooltip title="Comments" key="comment">
          <Button type="text" size="small" icon={<CommentOutlined />} />
        </Tooltip>,
        <Tooltip title="More" key="more">
          <Button type="text" size="small" icon={<MoreOutlined />} />
        </Tooltip>,
      ]}
    >
      <List.Item.Meta
        avatar={
          <Checkbox
            checked={task.completed}
            onChange={() => onToggle(task.id)}
          />
        }
        title={
          <span
            className={`text-base ${
              task.completed ? "line-through text-gray-400" : ""
            }`}
          >
            {task.title}
          </span>
        }
        description={
          <div className="flex flex-col">
            {task.description && (
              <Text type="secondary" className="text-sm">
                {task.description}
              </Text>
            )}
            <div className="flex items-center gap-2 mt-1">
              {task.deadline && (
                <Text type="danger" className="text-xs">
                  📅 {dayjs(task.deadline).format("DD MMM")}
                </Text>
              )}
              {task.project && (
                <Tag color="default" className="text-xs">
                  {task.project}
                </Tag>
              )}
              {task.commentCount > 0 && (
                <Text type="secondary" className="text-xs">
                  💬 {task.commentCount}
                </Text>
              )}
            </div>
          </div>
        }
      />
    </List.Item>
  );

  return (
    <div className="p-6">
      {/* Overdue Section */}
      {overdue.length > 0 && (
        <>
          <h3 className="font-semibold text-sm text-gray-500 mb-2">Overdue</h3>
          <List dataSource={overdue} renderItem={renderTask} />
          <Divider />
        </>
      )}

      {/* Today Section */}
      <h3 className="font-semibold text-sm text-gray-500 mb-2">
        {dayjs().format("D MMM · dddd")}
      </h3>
      <List dataSource={today} renderItem={renderTask} />
    </div>
  );
}
