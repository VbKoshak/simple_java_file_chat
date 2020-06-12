package automation.constant;

public class MessageConstants {
    public static final String CLIENT_READY_CHAR = "c";
    public static final String SERVER_READY_CHAR = "s";

    public static final String REGISTER_MESSAGE_HEAD = "RegisterMessage";
    public static final String HISTORY_MESSAGE_HEAD = "HistoryMessage";
    public static final String RESPONSE_MESSAGE_HEAD = "ResponseMessage";
    public static final String SIMPLE_MESSAGE_HEAD = "ConnectMessage";

    public static final String CLOSE_MESSAGE_TYPE = "close";
    public static final String HISTORY_MESSAGE_TYPE = "history";
    public static final String SIMPLE_MESSAGE_TYPE = "message";


    public static final String CONNECTION_FAILED_INFO = "Connection failed";
    public static final String CONNECTION_SUCCESS_INFO = "Connection success";

    /**
     * Client
     */

    public static final String SUCCESSFUL_REGISTRATION_MESSAGE = "Registration complete";
    public static final String FAILED_REGISTRATION_MESSAGE = "Registration failed";

    public static final String READY_MESSAGE_INPUT_INFO = "Enter message: ";

    public static final String ON_CONNECT_CLOSE = "disconnect";

    /**
     * Server
     */

    public static final String HISTORY_FOUND_INFO = "Loaded history";
    public static final String SERVER_READY_TO_WORK = "Server ready";

    public static final String ON_REGISTER_SUCCESS = "Registration complete";
    public static final String ON_REGISTER_FAILED = "Registration failed";
    public static final String ON_CRITICAL_ERROR = "server crashed: reload needed";

    public static final String ON_THREAD_CREATION = "new thread created";
    public static final String ON_READ_ERROR = "Can not read file";

}