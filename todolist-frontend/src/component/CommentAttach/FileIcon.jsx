
export const FileIcon = ({ type }) => {
    switch ((type || "").toLowerCase()) {
        case 'pdf':
            return <i className="fa fa-file-pdf text-red-500"></i>;
        case 'docx':
        case 'doc':
            return <i className="fa fa-file-word text-blue-600"></i>;
        case 'xlsx':
            return <i className="fa fa-file-excel text-green-600"></i>;
        default:
            return <i className="fa fa-file text-gray-500"></i>;
    }
};
