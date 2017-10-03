package sg.lifecare.jsw.util;

import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_CONNECTED;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_CONNECTE_COUNTDOWN;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_CONNECTING;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_CONNECT_FAIL;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_CONNECT_WRONG_DID;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_CONNECT_WRONG_PWD;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_NOT_INITIALIZED;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_NO_NETWORK;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_SESSION_CLOSED;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_UNKNOWN;
import static com.jsw.sdk.p2p.device.P2PDev.OM_GET_ONE_PIC_FROM_STREAM;
import static com.jsw.sdk.p2p.device.P2PDev.OM_GET_ONE_PIC_TIMEOUT;
import static com.jsw.sdk.p2p.device.P2PDev.OM_IOCTRL_RECORD_PLAY_END;
import static com.jsw.sdk.p2p.device.P2PDev.OM_RECV_DOWNLOAD_RESP_TIMEOUT;
import static com.jsw.sdk.p2p.device.P2PDev.OM_SHOW_DEVICE_VIDEO;
import static com.jsw.sdk.p2p.device.P2PDev.OM_SHOW_OFFLINE_PIC;
import static com.jsw.sdk.p2p.device.P2PDev.OM_SNAPSHOT_DECODE;
import static com.jsw.sdk.p2p.device.P2PDev.OM_UPDATE_LAST_PIC_FROM_STREAM;
import static com.jsw.sdk.p2p.device.P2PDev.STATUS_INFO_AV_ONLINENUM;
import static com.jsw.sdk.p2p.device.P2PDev.STATUS_INFO_AV_RESOLUTION;
import static com.jsw.sdk.p2p.device.P2PDev.STATUS_INFO_CAM_INDEX_FROM_DEV;
import static com.jsw.sdk.p2p.device.P2PDev.STATUS_INFO_GET_FRAME;
import static com.jsw.sdk.p2p.device.P2PDev.STATUS_INFO_LAST_I_FRAME;
import static com.jsw.sdk.p2p.device.P2PDev.STATUS_INFO_REMOTE_RECORDING;
import static com.jsw.sdk.p2p.device.P2PDev.STATUS_INFO_TRY_AGAIN_LATER;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_AUDIO_START;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_AUDIO_STOP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_AUTH_ADMIN_PASSWORD_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_AUTH_ADMIN_PASSWORD_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_AUTO_DEL_REC_ON_OFF_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_DEVINFO_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_DEVINFO_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_EMAIL_ON_OFF_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_EMAIL_ON_OFF_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_EVENT_NOTIFY;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_EVENT_NOTIFY_ON_OFF_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_EVENT_NOTIFY_ON_OFF_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_EXTERN_GPIO_CTRL_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_EXTERN_GPIO_CTRL_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_FORMATEXTSTORAGE_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_FORMATEXTSTORAGE_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GETMOTIONDETECT_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GETMOTIONDETECT_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GETWIFI_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GETWIFI_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_ARM_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_ARM_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_MOTION_SENSITIVITY_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_MOTION_SENSITIVITY_RESP;

import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_ONET_DEVINFO_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_ONET_DEVINFO_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_ON_OFF_VALUE_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_ON_OFF_VALUE_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_TIMEZONE_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_TIMEZONE_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_VIDEO_PARAMETER_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_VIDEO_PARAMETER_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_LISTEVENT_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_LISTEVENT_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_LISTWIFIAP_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_LISTWIFIAP_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_MANU_REC_START;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_MANU_REC_STOP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_PTZ_COMMAND;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_PUSH_APP_UTC_TIME;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_PUSH_CamIndex;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_RECORD_PLAYCONTROL_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_RECORD_PLAYCONTROL_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_REMOVE_EVENTLIST_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_REMOVE_EVENTLIST_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_REMOVE_EVENT_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_REMOVE_EVENT_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SEND_FIRMWARE_IMAGE_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SEND_FIRMWARE_IMAGE_STATUS_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SEND_FIRMWARE_IMAGE_STATUS_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SETMOTIONDETECT_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SETMOTIONDETECT_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SETPASSWORD_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SETPASSWORD_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SETWIFI_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SETWIFI_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_ADMIN_PASSWORD_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_ADMIN_PASSWORD_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_ARM_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_ARM_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_MOTION_SENSITIVITY_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_ONET_DEVINFO_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_ONET_DEVINFO_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_ONET_STATUS_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_ONET_STATUS_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_SYSTEM_REBOOT_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_TIMEZONE_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_TIMEZONE_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_VIDEO_PARAMETER_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SET_VIDEO_PARAMETER_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SPEAKER_START;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_SPEAKER_STOP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_UPGRADE_FIRMWARE_REQ;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_UPGRADE_FIRMWARE_RESP;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_VIDEO_START;
import static com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto.IOCTRL_TYPE_VIDEO_STOP;

public class JswUtil {

    public static String getFormatedDeviceId(byte[] devId) {
        if(devId == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder("");
        boolean bIsCharacter = true;

        for(int iLen=0; iLen < devId.length; iLen++) {
            if(devId[iLen] == (byte)0) {
                break;
            }

            if(devId[iLen] >= 0x30 && devId[iLen] <= 0x39) {
                if(bIsCharacter){
                    bIsCharacter = false;
                    sb.append('-');
                }
                sb.append((char)devId[iLen]);

            } else if (devId[iLen]>=0x61 && devId[iLen]<=0x7A){ //'a'--'z'
                if(!bIsCharacter){
                    bIsCharacter = true;
                    sb.append('-');
                }
                sb.append((char)(devId[iLen]-0x61+0x41));

            } else if (devId[iLen]>=0x41 && devId[iLen]<=0x5A){ //'A'--'Z'
                if(!bIsCharacter) {
                        bIsCharacter = true;
                        sb.append('-');
                }
                sb.append((char)devId[iLen]);

            } else if(devId[iLen] == '-') {

            } else {
                break;
            }
        }//for-end
        return sb.toString();
    }

    public static String getP2pDevResponseString(int code) {
        switch (code) {
            case CONN_INFO_UNKNOWN:
                return "CONN_INFO_UNKNOWN";

            case CONN_INFO_CONNECTING:
                return "CONN_INFO_CONNECTING";

            case CONN_INFO_NO_NETWORK:
                return "CONN_INFO_NO_NETWORK";

            case CONN_INFO_CONNECT_WRONG_DID:
                return "CONN_INFO_CONNECT_WRONG_DID";

            case CONN_INFO_CONNECT_WRONG_PWD:
                return "CONN_INFO_CONNECT_WRONG_PWD";

            case CONN_INFO_CONNECT_FAIL:
                return "CONN_INFO_CONNECT_FAIL";

            case CONN_INFO_SESSION_CLOSED:
                return "CONN_INFO_SESSION_CLOSED";

            case CONN_INFO_NOT_INITIALIZED:
                return "CONN_INFO_NOT_INITIALIZED";

            case CONN_INFO_CONNECTE_COUNTDOWN:
                return "CONN_INFO_CONNECTE_COUNTDOWN";

            case CONN_INFO_CONNECTED:
                return "CONN_INFO_CONNECTED";

            case STATUS_INFO_AV_ONLINENUM:
                return "STATUS_INFO_AV_ONLINENUM";

            case STATUS_INFO_AV_RESOLUTION:
                return "STATUS_INFO_AV_RESOLUTION";

            case STATUS_INFO_CAM_INDEX_FROM_DEV:
                return "STATUS_INFO_CAM_INDEX_FROM_DEV";

            case STATUS_INFO_REMOTE_RECORDING:
                return "STATUS_INFO_REMOTE_RECORDING";

            case STATUS_INFO_GET_FRAME:
                return "STATUS_INFO_GET_FRAME";

            case STATUS_INFO_TRY_AGAIN_LATER:
                return "STATUS_INFO_TRY_AGAIN_LATER";

            case STATUS_INFO_LAST_I_FRAME:
                return "STATUS_INFO_LAST_I_FRAME";

            case OM_GET_ONE_PIC_FROM_STREAM:
                return "OM_GET_ONE_PIC_FROM_STREAM";

            case OM_IOCTRL_RECORD_PLAY_END:
                return "OM_IOCTRL_RECORD_PLAY_END";

            case OM_SHOW_DEVICE_VIDEO:
                return "OM_SHOW_DEVICE_VIDEO";

            case OM_SHOW_OFFLINE_PIC:
                return "OM_SHOW_OFFLINE_PIC";

            case OM_GET_ONE_PIC_TIMEOUT:
                return "OM_GET_ONE_PIC_TIMEOUT";

            case OM_RECV_DOWNLOAD_RESP_TIMEOUT:
                return "OM_RECV_DOWNLOAD_RESP_TIMEOUT";

            case OM_SNAPSHOT_DECODE:
                return "OM_SNAPSHOT_DECODE";

            case OM_UPDATE_LAST_PIC_FROM_STREAM:
                return "OM_UPDATE_LAST_PIC_FROM_STREAM";

            default:
                return "UNKNOWN";
        }
    }

    public static String getIoctlResponseString(int code) {
        switch (code) {
            case IOCTRL_TYPE_PUSH_CamIndex:
                return "IOCTRL_TYPE_PUSH_CamIndex";

            case IOCTRL_TYPE_VIDEO_START:
                return "IOCTRL_TYPE_VIDEO_START";

            case IOCTRL_TYPE_VIDEO_STOP:
                return "IOCTRL_TYPE_VIDEO_STOP";

            case IOCTRL_TYPE_AUDIO_START:
                return "IOCTRL_TYPE_AUDIO_START";

            case IOCTRL_TYPE_AUDIO_STOP:
                return "IOCTRL_TYPE_AUDIO_STOP";

            case IOCTRL_TYPE_SET_ARM_REQ:
                return "IOCTRL_TYPE_SET_ARM_REQ";

            case IOCTRL_TYPE_SET_ARM_RESP:
                return "IOCTRL_TYPE_SET_ARM_RESP";

            case IOCTRL_TYPE_GET_ARM_REQ:
                return "IOCTRL_TYPE_GET_ARM_REQ";

            case IOCTRL_TYPE_GET_ARM_RESP:
                return "IOCTRL_TYPE_GET_ARM_RESP";

            case IOCTRL_TYPE_EXTERN_GPIO_CTRL_REQ:
                return "IOCTRL_TYPE_EXTERN_GPIO_CTRL_REQ";

            case IOCTRL_TYPE_EXTERN_GPIO_CTRL_RESP:
                return "IOCTRL_TYPE_EXTERN_GPIO_CTRL_RESP";

            case IOCTRL_TYPE_DEVINFO_REQ:
                return "IOCTRL_TYPE_DEVINFO_REQ";

            case IOCTRL_TYPE_DEVINFO_RESP:
                return "IOCTRL_TYPE_DEVINFO_RESP";

            case IOCTRL_TYPE_RECORD_PLAYCONTROL_REQ:
                return "IOCTRL_TYPE_RECORD_PLAYCONTROL_REQ";

            case IOCTRL_TYPE_RECORD_PLAYCONTROL_RESP:
                return "IOCTRL_TYPE_RECORD_PLAYCONTROL_RESP";

            case IOCTRL_TYPE_PTZ_COMMAND:
                return "IOCTRL_TYPE_PTZ_COMMAND";

            case IOCTRL_TYPE_LISTEVENT_REQ:
                return "IOCTRL_TYPE_LISTEVENT_REQ";

            case IOCTRL_TYPE_LISTEVENT_RESP:
                return "IOCTRL_TYPE_LISTEVENT_RESP";

            case IOCTRL_TYPE_EVENT_NOTIFY:
                return "IOCTRL_TYPE_EVENT_NOTIFY";

            case IOCTRL_TYPE_EMAIL_ON_OFF_REQ:
                return "IOCTRL_TYPE_EMAIL_ON_OFF_REQ";

            case IOCTRL_TYPE_EMAIL_ON_OFF_RESP:
                return "IOCTRL_TYPE_EMAIL_ON_OFF_RESP";

            case IOCTRL_TYPE_EVENT_NOTIFY_ON_OFF_REQ:
                return "IOCTRL_TYPE_EVENT_NOTIFY_ON_OFF_REQ";

            case IOCTRL_TYPE_EVENT_NOTIFY_ON_OFF_RESP:
                return "IOCTRL_TYPE_EVENT_NOTIFY_ON_OFF_RESP";

            case IOCTRL_TYPE_GET_ON_OFF_VALUE_REQ:
                return "IOCTRL_TYPE_GET_ON_OFF_VALUE_REQ";

            case IOCTRL_TYPE_GET_ON_OFF_VALUE_RESP:
                return "IOCTRL_TYPE_GET_ON_OFF_VALUE_RESP";

            case IOCTRL_TYPE_SPEAKER_START:
                return "IOCTRL_TYPE_SPEAKER_START";

            case IOCTRL_TYPE_SPEAKER_STOP:
                return "IOCTRL_TYPE_SPEAKER_STOP";

            case IOCTRL_TYPE_SETPASSWORD_REQ:
                return "IOCTRL_TYPE_SETPASSWORD_REQ";

            case IOCTRL_TYPE_SETPASSWORD_RESP:
                return "IOCTRL_TYPE_SETPASSWORD_RESP";

            case IOCTRL_TYPE_SET_VIDEO_PARAMETER_REQ:
                return "IOCTRL_TYPE_SET_VIDEO_PARAMETER_REQ";

            case IOCTRL_TYPE_SET_VIDEO_PARAMETER_RESP:
                return "IOCTRL_TYPE_SET_VIDEO_PARAMETER_RESP";

            case IOCTRL_TYPE_GET_VIDEO_PARAMETER_REQ:
                return "IOCTRL_TYPE_GET_VIDEO_PARAMETER_REQ";

            case IOCTRL_TYPE_GET_VIDEO_PARAMETER_RESP:
                return "IOCTRL_TYPE_GET_VIDEO_PARAMETER_RESP";

            case IOCTRL_TYPE_LISTWIFIAP_REQ:
                return "IOCTRL_TYPE_LISTWIFIAP_REQ";

            case IOCTRL_TYPE_LISTWIFIAP_RESP:
                return "IOCTRL_TYPE_LISTWIFIAP_RESP";

            case IOCTRL_TYPE_SETWIFI_REQ:
                return "IOCTRL_TYPE_SETWIFI_REQ";

            case IOCTRL_TYPE_SETWIFI_RESP:
                return "IOCTRL_TYPE_SETWIFI_RESP";

            case IOCTRL_TYPE_SETMOTIONDETECT_REQ:
                return "IOCTRL_TYPE_SETMOTIONDETECT_REQ";

            case IOCTRL_TYPE_SETMOTIONDETECT_RESP:
                return "IOCTRL_TYPE_SETMOTIONDETECT_RESP";

            case IOCTRL_TYPE_GETMOTIONDETECT_REQ:
                return "IOCTRL_TYPE_GETMOTIONDETECT_REQ";

            case IOCTRL_TYPE_GETMOTIONDETECT_RESP:
                return "IOCTRL_TYPE_GETMOTIONDETECT_RESP";

            case IOCTRL_TYPE_FORMATEXTSTORAGE_REQ:
                return "IOCTRL_TYPE_FORMATEXTSTORAGE_REQ";

            case IOCTRL_TYPE_FORMATEXTSTORAGE_RESP:
                return "IOCTRL_TYPE_FORMATEXTSTORAGE_RESP";

            case IOCTRL_TYPE_MANU_REC_START:
                return "IOCTRL_TYPE_MANU_REC_START";

            case IOCTRL_TYPE_MANU_REC_STOP:
                return "IOCTRL_TYPE_MANU_REC_STOP";

            case IOCTRL_TYPE_AUTH_ADMIN_PASSWORD_REQ:
                return "IOCTRL_TYPE_AUTH_ADMIN_PASSWORD_REQ";

            case IOCTRL_TYPE_AUTH_ADMIN_PASSWORD_RESP:
                return "IOCTRL_TYPE_AUTH_ADMIN_PASSWORD_RESP";

            case IOCTRL_TYPE_SET_ADMIN_PASSWORD_REQ:
                return "IOCTRL_TYPE_SET_ADMIN_PASSWORD_REQ";

            case IOCTRL_TYPE_SET_ADMIN_PASSWORD_RESP:
                return "IOCTRL_TYPE_SET_ADMIN_PASSWORD_RESP";

            case IOCTRL_TYPE_GETWIFI_REQ:
                return "IOCTRL_TYPE_GETWIFI_REQ";

            case IOCTRL_TYPE_GETWIFI_RESP:
                return "IOCTRL_TYPE_GETWIFI_RESP";

            case IOCTRL_TYPE_PUSH_APP_UTC_TIME:
                return "IOCTRL_TYPE_PUSH_APP_UTC_TIME";

            case IOCTRL_TYPE_SET_TIMEZONE_REQ:
                return "IOCTRL_TYPE_SET_TIMEZONE_REQ";

            case IOCTRL_TYPE_SET_TIMEZONE_RESP:
                return "IOCTRL_TYPE_SET_TIMEZONE_RESP";

            case IOCTRL_TYPE_GET_TIMEZONE_REQ:
                return "IOCTRL_TYPE_GET_TIMEZONE_REQ";

            case IOCTRL_TYPE_GET_TIMEZONE_RESP:
                return "IOCTRL_TYPE_GET_TIMEZONE_RESP";

            case IOCTRL_TYPE_AUTO_DEL_REC_ON_OFF_REQ:
                return "IOCTRL_TYPE_AUTO_DEL_REC_ON_OFF_REQ";

            case IOCTRL_TYPE_SET_MOTION_SENSITIVITY_REQ:
                return "IOCTRL_TYPE_SET_MOTION_SENSITIVITY_REQ";

            case IOCTRL_TYPE_GET_MOTION_SENSITIVITY_REQ:
                return "IOCTRL_TYPE_GET_MOTION_SENSITIVITY_REQ";

            case IOCTRL_TYPE_GET_MOTION_SENSITIVITY_RESP:
                return "IOCTRL_TYPE_GET_MOTION_SENSITIVITY_RESP";

            case IOCTRL_TYPE_SET_SYSTEM_REBOOT_REQ:
                return "IOCTRL_TYPE_SET_SYSTEM_REBOOT_REQ";

            case IOCTRL_TYPE_GET_ONET_DEVINFO_REQ:
                return "IOCTRL_TYPE_GET_ONET_DEVINFO_REQ";

            case IOCTRL_TYPE_GET_ONET_DEVINFO_RESP:
                return "IOCTRL_TYPE_GET_ONET_DEVINFO_RESP";

            case IOCTRL_TYPE_SET_ONET_DEVINFO_REQ:
                return "IOCTRL_TYPE_SET_ONET_DEVINFO_REQ";

            case IOCTRL_TYPE_SET_ONET_DEVINFO_RESP:
                return "IOCTRL_TYPE_SET_ONET_DEVINFO_RESP";

            case IOCTRL_TYPE_SET_ONET_STATUS_REQ:
                return "IOCTRL_TYPE_SET_ONET_STATUS_REQ";

            case IOCTRL_TYPE_SET_ONET_STATUS_RESP:
                return "IOCTRL_TYPE_SET_ONET_STATUS_RESP";

            case IOCTRL_TYPE_REMOVE_EVENTLIST_REQ:
                return "IOCTRL_TYPE_REMOVE_EVENTLIST_REQ";

            case IOCTRL_TYPE_REMOVE_EVENTLIST_RESP:
                return "IOCTRL_TYPE_REMOVE_EVENTLIST_RESP";

            case IOCTRL_TYPE_REMOVE_EVENT_REQ:
                return "IOCTRL_TYPE_REMOVE_EVENT_REQ";

            case IOCTRL_TYPE_REMOVE_EVENT_RESP:
                return "IOCTRL_TYPE_REMOVE_EVENT_RESP";

            case IOCTRL_TYPE_UPGRADE_FIRMWARE_REQ:
                return "IOCTRL_TYPE_UPGRADE_FIRMWARE_REQ";

            case IOCTRL_TYPE_UPGRADE_FIRMWARE_RESP:
                return "IOCTRL_TYPE_UPGRADE_FIRMWARE_RESP";

            case IOCTRL_TYPE_SEND_FIRMWARE_IMAGE_STATUS_REQ:
                return "IOCTRL_TYPE_SEND_FIRMWARE_IMAGE_STATUS_REQ";

            case IOCTRL_TYPE_SEND_FIRMWARE_IMAGE_STATUS_RESP:
                return "IOCTRL_TYPE_SEND_FIRMWARE_IMAGE_STATUS_RESP";

            case IOCTRL_TYPE_SEND_FIRMWARE_IMAGE_REQ:
                return "IOCTRL_TYPE_SEND_FIRMWARE_IMAGE_REQ";

            default:
                return "UNKNOWN";
        }
    }
}
