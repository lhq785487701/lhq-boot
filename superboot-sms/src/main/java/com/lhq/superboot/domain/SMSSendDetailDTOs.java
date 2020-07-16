package com.lhq.superboot.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: lihaoqi
 * @date: 2019年4月30日
 * @version: 1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SMSSendDetailDTOs {

    private List<SMSSendDetailDTO> smsSendDetailDTO;
}
