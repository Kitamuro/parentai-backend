package ai.parent.parentaibackend.status;

import ai.parent.parentaibackend.baby.dto.BabySummaryDto;
import ai.parent.parentaibackend.user.dto.UserSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeDashboardResponse {

    private UserSummaryDto user;
    private List<BabySummaryDto> babies;

    /**
     * id выбранного ребёнка для отображения на главном экране.
     * Может быть null, если детей нет.
     */
    private Long selectedBabyId;

    /**
     * Дата, за которую построен todayStatus.
     */
    private LocalDate date;

    /**
     * Сводка по сну/кормлению/памперсам за выбранную дату и ребёнка.
     * Может быть null, если детей нет.
     */
    private TodayStatusResponse todayStatus;
}
