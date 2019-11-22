package com.szhq.iemp.common.constant;

public class CommonConstant {

//    public static final String INNER_ERROR = "内部错误,请联系管理员";
//    public static final String WRONG_PARAMETER = "传入参数不正确";
//    public static final String USER_ACCOUNT_NOT_EXIST = "用户账号不存在";
//    public static final String SPECIAL_CHARACTER = "传入参数包含特殊字符";

    public static final Integer WRONG_CODE = 500;
//    public static final Integer WRONG_PARAMETER_CODE = 400;
    public static final String DEVICE_MODE_310 = "W310";

    public static final String MAX_RESERVATION_NUM_KEY = "max-reservation";
    public static final String RESERVATION_IN_DAYS_KEY = "reservation-in-days";
    public static final String CURRENT_ENV_KEY = "current-env";
    public static final String LC_BROADCAST_KEY = "lc-broadcast";
    public static final String INSTRUCTION_KEY = "310-instruction-url";

    public static final String IEMP_NBIOT_TRACKER_IMEI = "iemp-nbiot-tracker-imei_";


    public static final String REGISTER_IMEI = "registerImei:";
    public static final String REGISTER_ID = "registerId:";
    public static final String REGISTER_IOTDEVICEID = "register-iotDeviceId:";
    public static final String ELEC_IMEI = "elec-imei:";
    public static final String ELEC_PLATENUMBER = "elec-plateno:";
    public static final String ELECALL_ID_KEY = "elecAll-id:";
    public static final String IOTDEVICEID = "iotDeviceId:";
    public static final String DEVICE_IMEI = "deviceImei:";
    public static final String DEVSTATE_IMEI = "dev-state-imei:";
    public static final String STATUS_IMEI = "status-";
    public static final String OPERATOR_REDISKEY = "operators:";
    public static final String USER_ID = "user:";
    public static final String ELEC_ID = "elec:";

    public static final String REGISTER_KEY = "register-";
    public static final String OPERATOR_IMEI_KEY = "operator-imei-";
    public static final String RTDATA_IMEI_KEY = "rtdata_";
    public static final String RT_WLANDATA_IMEI_KEY = "rt_wlan_data_";

    public static final String REGION_ID = "region-";

    public static final String REGISER_PATTERN = "aepregister::com.szhq.iemp.reservation.service.RegisterationServiceImplfindRegistrationCriteria*";
    public static final String RESERVATION_PATTERN = "aepreservation::com.szhq.iemp.reservation.service.ReservationServiceImplfindReservationCriteria*";
    public static final String ELEC_COLORS_PATTERN = "aepElectrombile::com.szhq.iemp.reservation.service.ElectrmobileServiceImplgetElecmobileColors";
    public static final String ELEC_TYPES_PATTERN = "aepElectrombile::com.szhq.iemp.reservation.service.ElectrmobileServiceImplgetElecmobileTypes";
    public static final String ELEC_VENDORS_PATTERN = "aepElectrombile::com.szhq.iemp.reservation.service.ElectrmobileServiceImplgetElecmobileVendors";
    public static final String ELEC_PATTERN = "aepElectrombile::com.szhq.iemp.reservation.service.ElectrmobileServiceImplfindElecByCriteria*";
    public static final String SITE_PATTERN = "aepInstallSite::com.szhq.iemp.device.service.InstallSiteServiceImpl*";
    public static final String DEVICE_PATTERN = "aepDeviceInventory::com.szhq.iemp.device.service.DeviceInventoryServiceImpl*";
    public static final String REGION_PATTERN = "aepAddressRegion::com.szhq.iemp.device.service.AddressRegionServiceImpl*";
    public static final String OPERATOR_PATTERN = "aepOperator::com.szhq.iemp.device.service.OperatorServiceImpl*";
    public static final String HISTORY_DISPACHE_LOG_PATTERN = "aepDispacheHistoryLog::com.szhq.iemp.device.service.DeviceDispacheHistoryServiceimpl*";
    public static final String NOTRACKER_PATTERN = "aepNoTrackerElec::com.szhq.iemp.reservation.service.NoTrackerElecmobileServiceImpl*";
    public static final String RT_DATA_KEY = "consumer-mysql::com.szhq.data.kafkaconsumer.service.slave.impl.NbiotDeviceRtDataServiceImplfindByImei";

    public static final String CMCC = "CMCC";
    public static final String CT = "CT";
    public static final String CUCC = "CUCC";


    public static final String DISPATCH = "dispatch";
    public static final String BACK_OFF = "backoff";

    public static final String DEVICE_ACTIVE_URL = "/activator/active";
    public static final String DEVICE_UN_ACTIVE_URL = "/activator/back";
    public static final String HISTORY_ACTIVE_COUNT = "/activator/historyActiveCount";

    public static final String ALARM_LIST_URL = "/alarm/list";

    public static final String REGISTER_URL = "/register/";
    public static final String REGISTER_CHANGEIMEI_URL = "/register/changeImei";
    public static final String REGISTER_DELETE_URL = "/register/delete";
    public static final String REGISTER_LOG_SEARCH = "/registrationLog/log";
    public static final String REGISTER_SEARCH_URL = "/register/search";
    public static final String REGISTER_COUNT = "/register/count";
    public static final String REGISTER_LIST_URL = "/register/list";

    public static final String NOTRACKER_SEARCH = "/notracker/search";
    public static final String NOTRACKER_ADD_REGISTER = "/notracker/addRegister";
    public static final String NOTRACKER_BOUND = "/notracker/boundDevice";
    //310
    public static final String NOTRACKER_UNBOUND_DEVICE = "/notracker/unboundDevice";
    public static final String NOTRACKER_UNBOUND = "/notracker/unbound";
    public static final String NOTRACKER_UNBOUND_NO_DELELEC = "/notracker/unboundDeviceNodelElec";

    public static final String ELECMOBILE_SEARCH_URL = "/electrombile/search";
    public static final String ELECMOBILE_SEARCHALL_URL = "/electrombile/searchAll";
    public static final String ELECMOBILE_GETELECINFO_BY_PLATENO = "/electrombile/getElecAndUserInfoByPlateNo";

    public static final String RESERVATION_SEARCH_URL = "/reservation/search";

    public static final String OPERATOR_SEARCH = "/operator/search";

    public static final String DISPACHERLOG_SEARCH_URL = "/dispacherlog/search";

    public static final String INSTALLSITE_SEARCH_URL = "/site/search";
    public static final String INSTALLSITE_COUNTALL_URL = "/site/countAll";
    public static final String INSTALLSITE_COUNT_URL = "/site/count";
    public static final String INSTALLSITE_COUNTINSTALLED_URL = "/site/countInstalled";
    public static final String STOREHOUSE_SEARCH_URL = "/storehouse/search";
    public static final String STOREHOUSE_ACTIVE_STATISTIC_URL = "/storehouse/deviceActiveStatistic";

    public static final String DEVICEINVENTORY_SEARCH_URL = "/deviceInventory/search";
    public static final String COUNT_OF_ISP_URL = "/deviceInventory/getCountOfIsp";
    public static final String PUTSTORAGE_STATISTIC_URL = "/deviceInventory/putStorageStatistic";
    public static final String BACKOFF_STATISTIC_URL = "/deviceInventory/backOffStatistic";
    public static final String DISPATCH_STATISTIC_URL = "/deviceInventory/dispatchStatistic";
    public static final String BACKOFF_BOXNUMBERS_URL = "/deviceInventory/getBackOffBoxNumbers";
    public static final String GET_DISPACHE_BOXNUMBERS_URL = "/deviceInventory/getDispatchBoxNumbers";
    public static final String GET_BACKOFF_BOXNUMBERS_URL = "/deviceInventory/getBackOffBoxNumbers";
    public static final String GET_PUTSTORAGE_BOXNUMBERS_URL = "/deviceInventory/getBoxNumbersOfPutStorage";
    public static final String GET_BOXNUMBERS_BY_PUTSTORAGETIME_URL = "/deviceInventory/getBoxNumberByPutStorageTime";
    public static final String VALID_PUTSTORAGE_BY_BOXNUMBERS_URL = "/deviceInventory/validPutStorageByBoxNumber";
    public static final String VALID_IMEI_INFO_URL = "/deviceInventory/validImeiInfo";
    public static final String DISPACHE_BY_IMEIS_URL = "/deviceInventory/dispatchByImeis";
    public static final String DISPACHE_BY_BOXNUMBER_URL = "/deviceInventory/dispatchByBoxNumbers";
    public static final String BACK_BY_BOXNUMBER_URL = "/deviceInventory/backByBoxNumbers";
    public static final String BACK_BY_IMEIS_URL = "/deviceInventory/backByImeis";
    public static final String PUT_INTO_STORAGE_URL = "/deviceInventory/putInStorageByBoxNumbers";

    public static final String DISPATCH_TO_DEVICE_GROUP_URL = "/group/dispatchToDeviceGroup";
    public static final String DISPATCH_TO_ELEC_GROUP_URL = "/group/dispatchToElecGroup";
    public static final String REMOVE_GROUP = "/group/removeGroup";

    public static final String DATA_COUNTBYCONTION_URL = "/data-presentation/countByCondition";
    public static final String DATA_INSTALLSITE_ORDER_URL = "/data-presentation/installSiteOrder";
    public static final String DATA_HISTORY_INSTALLED_STASTIC_URL = "/data-presentation/historyInstalledStatistics";
    public static final String DATA_INSTALLED_INFO_URL = "/data-presentation/installedInfo";
}
