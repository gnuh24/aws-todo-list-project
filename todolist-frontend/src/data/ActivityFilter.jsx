import {
    UserAddOutlined,
    EditOutlined,
    PlusCircleFilled,
    MessageFilled,
    EditFilled,
    CheckCircleFilled,
    DeleteFilled,
    CloseCircleFilled
} from "@ant-design/icons";

// Mảng filter dựa trên NotificationType
export const notificationFilterMap = {
    PROJECT_MEMBER_ADDED: {
        label: "Added members",
        icon: <UserAddOutlined className="text-green-500" />,
    },
    PROJECT_MEMBER_ROLE_UPDATED: {
        label: "Updated member roles",
        icon: <EditOutlined className="text-blue-500" />,
    },
    TASK_ASSIGNED: {
        label: "Assigned tasks",
        icon: <PlusCircleFilled className="text-green-500" />,
    },
    TASK_COMMENTED: {
        label: "Added comments",
        icon: <MessageFilled className="text-green-600" />,
    },
    TASK_UPDATED: {
        label: "Updated tasks",
        icon: <EditFilled className="text-blue-500" />,
    },
    TASK_COMPLETED: {
        label: "Completed tasks",
        icon: <CheckCircleFilled className="text-green-500" />,
    },
    PROJECT_DELETED: {
        label: "Deleted projects",
        icon: <DeleteFilled className="text-red-500" />,
    },
    REQUEST_ACCEPTED: {
        label: "Accepted invitations",
        icon: <CheckCircleFilled className="text-green-500" />,
    },
    REQUEST_DECLINED: {
        label: "Declined invitations",
        icon: <CloseCircleFilled className="text-gray-400" />,
    }
};
