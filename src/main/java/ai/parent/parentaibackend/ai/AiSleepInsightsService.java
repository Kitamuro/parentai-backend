package ai.parent.parentaibackend.ai;

import ai.parent.parentaibackend.ai.dto.AiSleepInsightsResponse;
import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.common.exception.ResourceNotFoundException;
import ai.parent.parentaibackend.tracking.sleep.SleepEvent;
import ai.parent.parentaibackend.tracking.sleep.SleepEventRepository;
import ai.parent.parentaibackend.tracking.sleep.SleepType;
import ai.parent.parentaibackend.user.CurrentUserService;
import ai.parent.parentaibackend.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AiSleepInsightsService {

    private final CurrentUserService currentUserService;
    private final BabyRepository babyRepository;
    private final SleepEventRepository sleepEventRepository;

    /**
     * Анализ сна за последние 3 дня (по умолчанию) относительно targetDate.
     * В будущем сюда можно будет вставить вызов реального LLM, сейчас — “умная заглушка”.
     */
    public AiSleepInsightsResponse getSleepInsights(Long babyId, LocalDate targetDate) {
        User currentUser = currentUserService.getCurrentUserOrThrow();

        Baby baby = babyRepository.findByIdAndUser(babyId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ребёнок с id=" + babyId + " не найден у текущего пользователя"
                ));

        LocalDate effectiveDate = targetDate != null ? targetDate : LocalDate.now();

        // Анализируем последние 3 дня: [effectiveDate-2; effectiveDate]
        LocalDate fromDate = effectiveDate.minusDays(2);
        LocalDate toDate = effectiveDate;

        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.plusDays(1).atStartOfDay().minusNanos(1);

        List<SleepEvent> events = sleepEventRepository
                .findByBabyAndStartTimeBetweenOrderByStartTimeAsc(baby, from, to);

        if (events.isEmpty()) {
            String babyAge = buildAgeDescription(baby.getDateOfBirth());
            String summary = """
                    За последние несколько дней я не вижу записей сна для этого ребёнка.
                    Чтобы я мог давать более точные рекомендации, попробуйте вносить засыпания и пробуждения хотя бы в течение пары дней подряд.
                    """;

            List<String> hints = List.of(
                    "Отмечайте начало и конец каждого сна — так будет проще увидеть режим.",
                    "Особенно полезно отслеживать время бодрствования между снами.",
                    "Мои ответы не заменяют врача. При беспокойстве по поводу сна лучше обсудить это с педиатром."
            );

            return new AiSleepInsightsResponse(
                    baby.getId(),
                    baby.getName(),
                    babyAge,
                    fromDate,
                    toDate,
                    0,
                    0,
                    0,
                    summary,
                    hints
            );
        }

        long totalMinutes = 0;
        int totalEvents = 0;
        long dayMinutes = 0;
        long nightMinutes = 0;

        for (SleepEvent event : events) {
            if (event.getEndTime() == null) continue;
            long minutes = Duration.between(event.getStartTime(), event.getEndTime()).toMinutes();
            if (minutes <= 0) continue;

            totalMinutes += minutes;
            totalEvents++;

            if (event.getType() == SleepType.DAY) {
                dayMinutes += minutes;
            } else if (event.getType() == SleepType.NIGHT) {
                nightMinutes += minutes;
            }
        }

        long daysCount = 3; // fromDate..toDate всегда 3 календарных дня
        long avgPerDay = daysCount > 0 ? totalMinutes / daysCount : 0;

        String babyAge = buildAgeDescription(baby.getDateOfBirth());

        String summaryText = buildSummaryText(
                baby.getName(),
                babyAge,
                totalMinutes,
                avgPerDay,
                dayMinutes,
                nightMinutes,
                totalEvents
        );

        List<String> hints = buildHints(avgPerDay, dayMinutes, nightMinutes);

        return new AiSleepInsightsResponse(
                baby.getId(),
                baby.getName(),
                babyAge,
                fromDate,
                toDate,
                totalMinutes,
                avgPerDay,
                totalEvents,
                summaryText,
                hints
        );
    }

    private String buildSummaryText(
            String babyName,
            String babyAge,
            long totalMinutes,
            long avgPerDay,
            long dayMinutes,
            long nightMinutes,
            int totalEvents
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("Небольшой обзор сна ");

        if (babyName != null && !babyName.isBlank()) {
            sb.append("для ребёнка ").append(babyName);
        } else {
            sb.append("для вашего ребёнка");
        }

        if (babyAge != null) {
            sb.append(" (возраст: ").append(babyAge).append(")");
        }
        sb.append(" за последние несколько дней.\n\n");

        sb.append("• Общее время сна за период: ")
                .append(totalMinutes).append(" минут (≈ ")
                .append(totalMinutes / 60).append(" часов).\n");
        sb.append("• В среднем в сутки: ")
                .append(avgPerDay).append(" минут (≈ ")
                .append(avgPerDay / 60).append(" часов).\n");
        sb.append("• Дневной сон: ")
                .append(dayMinutes).append(" минут.\n");
        sb.append("• Ночной сон: ")
                .append(nightMinutes).append(" минут.\n");
        sb.append("• Количество эпизодов сна: ")
                .append(totalEvents).append(".\n\n");

        sb.append("Важно помнить, что точные нормы сна зависят от возраста и индивидуальных особенностей ребёнка. ");
        sb.append("Смотрите не только на суммарные часы, но и на то, насколько предсказуемым становится режим: ");
        sb.append("примерно одинаковое время засыпания, похожие интервалы бодрствования.\n\n");

        sb.append("Мои подсказки не являются медицинской рекомендацией. ");
        sb.append("Если у вас есть сомнения по поводу сна или поведения ребёнка, лучше обсудить это с педиатром.");

        return sb.toString();
    }

    private List<String> buildHints(long avgPerDay, long dayMinutes, long nightMinutes) {
        List<String> hints = new ArrayList<>();

        if (avgPerDay < 600) { // < 10 часов
            hints.add("Похоже, что суммарный сон за сутки может быть немного ниже типичных значений. Проверьте, нет ли хронического переутомления (долгое укладывание, частый плач).");
        } else if (avgPerDay > 900) { // > 15 часов
            hints.add("Сна получается довольно много. Если ребёнок бодр и развитие идёт в норме, это может быть его индивидуальной нормой. При сомнениях обсудите это с врачом.");
        } else {
            hints.add("Общее количество сна за сутки похоже на типичные значения для многих детей данного возраста.");
        }

        if (dayMinutes > nightMinutes) {
            hints.add("Дневного сна получается больше, чем ночного. Иногда это приводит к трудностям с укладыванием вечером — можно попробовать слегка сократить дневные сны или сдвинуть их по времени.");
        } else {
            hints.add("Ночного сна больше, чем дневного — это хороший сигнал, постепенно ребёнок смещает основной сон в ночь.");
        }

        hints.add("Старайтесь обращать внимание на “окна бодрствования” — время от пробуждения до следующего сна. При первых признаках усталости лучше начать подготовку ко сну, а не ждать сильного переутомления.");

        return hints;
    }

    private String buildAgeDescription(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return null;
        }
        LocalDate now = LocalDate.now();
        if (dateOfBirth.isAfter(now)) {
            return null;
        }
        Period p = Period.between(dateOfBirth, now);

        int years = p.getYears();
        int months = p.getMonths();

        if (years == 0 && months == 0) {
            return "меньше месяца";
        }

        StringBuilder sb = new StringBuilder();
        if (years > 0) {
            sb.append(years).append(" ").append(yearWord(years));
        }
        if (months > 0) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append(months).append(" ").append(monthWord(months));
        }
        return sb.toString();
    }

    private String yearWord(int years) {
        int last = years % 10;
        int last2 = years % 100;
        if (last == 1 && last2 != 11) return "год";
        if (last >= 2 && last <= 4 && !(last2 >= 12 && last2 <= 14)) return "года";
        return "лет";
    }

    private String monthWord(int months) {
        int last = months % 10;
        int last2 = months % 100;
        if (last == 1 && last2 != 11) return "месяц";
        if (last >= 2 && last <= 4 && !(last2 >= 12 && last2 <= 14)) return "месяца";
        return "месяцев";
    }
}
