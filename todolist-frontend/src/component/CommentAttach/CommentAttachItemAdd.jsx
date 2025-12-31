import {DeleteOutlined} from "@ant-design/icons";
import { Image } from "antd";
import {BASE_URL} from "../../service/api";


export function CommentAttachItemAdd({ attachments, setAttachments }) {
    return (
        <div className="flex flex-wrap gap-2 mt-2 w-full">
            <Image.PreviewGroup>
                {attachments.map((idFile, idx) => (
                    <div
                        key={idx}
                        className="w-40 h-40 relative border rounded overflow-hidden bg-gray-50"
                    >
                        <Image
                            src={
                                idFile
                                    ? `${BASE_URL}/media/v1/local/${idFile}`
                                    : "/file-broken.png"
                            }
                            alt="attachment"
                            className="w-full h-full object-cover cursor-pointer"
                            preview
                        />

                        {/* Nút xóa */}
                        <button
                            onClick={(e) => {
                                e.stopPropagation(); // ❗ không trigger preview
                                setAttachments(prev =>
                                    prev.filter((_, i) => i !== idx)
                                );
                            }}
                            className="absolute top-1 right-1 w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center hover:bg-red-600 z-10"
                        >
                            <DeleteOutlined style={{ fontSize: 12 }} />
                        </button>
                    </div>
                ))}
            </Image.PreviewGroup>
        </div>
    );
}
