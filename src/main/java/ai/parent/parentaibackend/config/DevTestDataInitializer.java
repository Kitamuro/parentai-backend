package ai.parent.parentaibackend.config;

import ai.parent.parentaibackend.baby.Baby;
import ai.parent.parentaibackend.baby.BabyRepository;
import ai.parent.parentaibackend.tracking.diaper.DiaperEntry;
import ai.parent.parentaibackend.tracking.diaper.DiaperEntryRepository;
import ai.parent.parentaibackend.tracking.diaper.DiaperType;
import ai.parent.parentaibackend.tracking.feeding.FeedingEvent;
import ai.parent.parentaibackend.tracking.feeding.FeedingEventRepository;
import ai.parent.parentaibackend.tracking.feeding.FeedingType;
import ai.parent.parentaibackend.tracking.sleep.SleepEvent;
import ai.parent.parentaibackend.tracking.sleep.SleepEventRepository;
import ai.parent.parentaibackend.tracking.sleep.SleepType;
import ai.parent.parentaibackend.user.User;
import ai.parent.parentaibackend.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@Profile("dev") // будет работать только в профиле "dev"
public class DevTestDataInitializer {

    @Bean
    public CommandLineRunner initTestData(UserRepository userRepository,
                                          PasswordEncoder passwordEncoder,
                                          BabyRepository babyRepository,
                                          SleepEventRepository sleepEventRepository,
                                          FeedingEventRepository feedingEventRepository,
                                          DiaperEntryRepository diaperEntryRepository) {
        return args -> {
            // 1. Тестовый пользователь
            String testEmail = "test@parentai.app";

            User user = userRepository.findByEmail(testEmail).orElse(null);
            if (user == null) {
                user = new User();
                user.setEmail(testEmail);
                user.setFullName("Test User");
                user.setPasswordHash(passwordEncoder.encode("qwerty123"));
                user = userRepository.save(user);
                System.out.println("✅ Created test user: " + testEmail + " / qwerty123");
            } else {
                System.out.println("ℹ️ Test user already exists: " + testEmail);
            }

            // 2. Тестовый ребёнок
            Baby baby;
            if (babyRepository.count() == 0) {
                baby = new Baby();
                baby.setUser(user);
                baby.setName("Мия");
                baby.setDateOfBirth(LocalDate.now().minusMonths(9));
                baby.setGender("FEMALE");
                baby.setNotes("Тестовый ребёнок для dev");
                baby = babyRepository.save(baby);
                System.out.println("✅ Created test baby: " + baby.getName());
            } else {
                baby = babyRepository.findAll().get(0);
                System.out.println("ℹ️ Using existing baby: " + baby.getName());
            }

            // Проверим, есть ли уже события
            if (sleepEventRepository.count() > 0
                    || feedingEventRepository.count() > 0
                    || diaperEntryRepository.count() > 0) {
                System.out.println("ℹ️ Tracking data already exists, skip seeding.");
                return;
            }

            LocalDate today = LocalDate.now();
            LocalDateTime todayMorning = today.atTime(8, 0);
            LocalDateTime todayNoon = today.atTime(12, 30);
            LocalDateTime todayEvening = today.atTime(20, 30);

            // 3. Пара событий сна
            SleepEvent morningNap = new SleepEvent();
            morningNap.setBaby(baby);
            morningNap.setStartTime(todayMorning);
            morningNap.setEndTime(todayMorning.plusHours(1));
            morningNap.setType(SleepType.DAY);
            morningNap.setNotes("Утренний сон");
            sleepEventRepository.save(morningNap);

            SleepEvent nightSleep = new SleepEvent();
            nightSleep.setBaby(baby);
            nightSleep.setStartTime(today.minusDays(1).atTime(22, 30));
            nightSleep.setEndTime(today.atTime(7, 0));
            nightSleep.setType(SleepType.NIGHT);
            nightSleep.setNotes("Ночной сон");
            sleepEventRepository.save(nightSleep);

            // 4. Кормления
            FeedingEvent feeding1 = new FeedingEvent();
            feeding1.setBaby(baby);
            feeding1.setStartTime(todayMorning.minusMinutes(30));
            feeding1.setEndTime(todayMorning.minusMinutes(10));
            feeding1.setType(FeedingType.BREAST_LEFT);
            feeding1.setNotes("Кормление после пробуждения");
            feedingEventRepository.save(feeding1);

            FeedingEvent feeding2 = new FeedingEvent();
            feeding2.setBaby(baby);
            feeding2.setStartTime(todayNoon);
            feeding2.setType(FeedingType.FORMULA);
            feeding2.setVolumeMl(120);
            feeding2.setNotes("Смесь");
            feedingEventRepository.save(feeding2);

            // 5. Подгузники
            DiaperEntry diaper1 = new DiaperEntry();
            diaper1.setBaby(baby);
            diaper1.setTime(todayMorning.plusMinutes(15));
            diaper1.setType(DiaperType.WET);
            diaper1.setNotes("Перед прогулкой");
            diaperEntryRepository.save(diaper1);

            DiaperEntry diaper2 = new DiaperEntry();
            diaper2.setBaby(baby);
            diaper2.setTime(todayEvening.minusMinutes(20));
            diaper2.setType(DiaperType.MIXED);
            diaper2.setNotes("Перед купанием");
            diaperEntryRepository.save(diaper2);

            System.out.println("✅ Seeded test tracking data for baby: " + baby.getName());
        };
    }
}
