python2java = {
    "src_ip": None,
    "dst_ip" : None,
    "src_port": None,
    "dst_port": "Destination Port",
    "protocol": None,
    "timestamp": None,
    
    "flow_duration": "Flow Duration",
    "flow_byts_s": "Flow Bytes/s",
    "flow_pkts_s": "Flow Packets/s",
    "fwd_pkts_s": "Fwd Packets/s",
    "bwd_pkts_s": "Bwd Packets/s",
    
    "tot_fwd_pkts": "Total Fwd Packets",
    "tot_bwd_pkts": "Total Backward Packets",
    "totlen_fwd_pkts": "Total Length of Fwd Packets",
    "totlen_bwd_pkts": "Total Length of Bwd Packets",
    
    "fwd_pkt_len_max": "Fwd Packet Length Max",
    "fwd_pkt_len_min": "Fwd Packet Length Min",
    "fwd_pkt_len_mean": "Fwd Packet Length Mean",
    "fwd_pkt_len_std": "Fwd Packet Length Std",
    
    "bwd_pkt_len_max": "Bwd Packet Length Max",
    "bwd_pkt_len_min": "Bwd Packet Length Min",
    "bwd_pkt_len_mean": "Bwd Packet Length Mean",
    "bwd_pkt_len_std": "Bwd Packet Length Std",
    
    "pkt_len_max": "Max Packet Length",
    "pkt_len_min": "Min Packet Length",
    "pkt_len_mean": "Packet Length Mean",
    "pkt_len_std": "Packet Length Std",
    "pkt_len_var": "Packet Length Variance",
    
    "fwd_header_len": "Fwd Header Length",
    "bwd_header_len": "Bwd Header Length",
    "fwd_seg_size_min": "min_seg_size_forward",
    "fwd_act_data_pkts": "act_data_pkt_fwd",
    
    "flow_iat_mean": "Flow IAT Mean",
    "flow_iat_max": "Flow IAT Max",
    "flow_iat_min": "Flow IAT Min",
    "flow_iat_std": "Flow IAT Std",

    "fwd_iat_tot": "Fwd IAT Total",
    "fwd_iat_max": "Fwd IAT Max",
    "fwd_iat_min": "Fwd IAT Min",
    "fwd_iat_mean": "Fwd IAT Mean",
    "fwd_iat_std": "Fwd IAT Std",
    
    "bwd_iat_tot": "Bwd IAT Total",
    "bwd_iat_max": "Bwd IAT Max",
    "bwd_iat_min": "Bwd IAT Min",
    "bwd_iat_mean": "Bwd IAT Mean",
    "bwd_iat_std": "Bwd IAT Std",

    "fwd_psh_flags": "Fwd PSH Flags",
    "bwd_psh_flags": "Bwd PSH Flags",
    
    "fwd_urg_flags": "Fwd URG Flags",
    "bwd_urg_flags": "Bwd URG Flags",
    
    "fin_flag_cnt":  "FIN Flag Count",
    "syn_flag_cnt":  "SYN Flag Count",
    "rst_flag_cnt":  "RST Flag Count",
    "psh_flag_cnt":  "PSH Flag Count",
    "ack_flag_cnt":  "ACK Flag Count",
    "urg_flag_cnt":  "URG Flag Count",
    "ece_flag_cnt":  "ECE Flag Count",
    
    "down_up_ratio": "Down/Up Ratio",
    "pkt_size_avg": "Average Packet Size",
    "init_fwd_win_byts": "Init_Win_bytes_forward",
    "init_bwd_win_byts": "Init_Win_bytes_backward",
    
    "active_max": "Active Max",
    "active_min": "Active Min",
    "active_mean": "Active Mean",
    "active_std": "Active Std",
    
    "idle_max": "Idle Max",
    "idle_min": "Idle Min",
    "idle_mean": "Idle Mean",
    "idle_std": "Idle Std",
    
    "fwd_byts_b_avg": "Fwd Avg Bytes/Bulk",
    "fwd_pkts_b_avg": "Fwd Avg Packets/Bulk",
    
    "bwd_byts_b_avg": "Bwd Avg Bytes/Bulk",
    "bwd_pkts_b_avg": "Bwd Avg Packets/Bulk",
    
    "fwd_blk_rate_avg": "Fwd Avg Bulk Rate",
    "bwd_blk_rate_avg": "Bwd Avg Bulk Rate",
    
    "fwd_seg_size_avg": "Avg Fwd Segment Size",
    "bwd_seg_size_avg": "Avg Bwd Segment Size",
    
    "subflow_fwd_pkts": "Subflow Fwd Packets",
    "subflow_bwd_pkts": "Subflow Bwd Packets",
    
    "subflow_fwd_byts": "Subflow Fwd Bytes",
    "subflow_bwd_byts": "Subflow Bwd Bytes",
    
    "cwe_flag_count": "CWE Flag Count",
    "attack": "Label"
}

java2python = dict()
for k, v in python2java.items():
    if v is not None:
        java2python[v] = k
