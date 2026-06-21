package com.example.pharmacy.Configuration;

import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    CommandLineRunner seed(RolesRepository rolesRepository,
                           UserRepository userRepository,
                           PharmacyRepository pharmacyRepository,
                           AtcManualRepository atcManualRepository,
                           CategoryRepository categoryRepository,
                           NomenclatureRepository nomenclatureRepository,
                           BatchRepository batchRepository,
                           StockRepository stockRepository,
                           MarkingCodeRepository markingCodeRepository,
                           PasswordEncoder passwordEncoder) {
        return args -> {
            RoleBootstrap.migrateAndEnsure(rolesRepository);
            ensureTestUser(rolesRepository, userRepository, pharmacyRepository, passwordEncoder);
            ensureLeadershipUsers(rolesRepository, userRepository, pharmacyRepository, passwordEncoder);

            if (nomenclatureRepository.count() > 0) {
                return;
            }

            Roles pharmacist;
            Roles manager;
            Pharmacy p1;
            Pharmacy p2;

            if (userRepository.count() == 0) {
                pharmacist = rolesRepository.findByName(RoleName.PHARMACIST).orElseThrow();
                manager = rolesRepository.findByName(RoleName.MANAGER).orElseThrow();
                role(rolesRepository, RoleName.ADMIN, "Системный администратор");
                role(rolesRepository, RoleName.ACCOUNTANT, "Бухгалтер");
                role(rolesRepository, RoleName.DIRECTOR, "Директор");
                role(rolesRepository, RoleName.NETWORK_OWNER, "Владелец аптечной сети");
                role(rolesRepository, RoleName.TEST, "Тест: все разделы");

                p1 = pharmacy(pharmacyRepository, "Аптека №1 Центральная", "Муром, ул. Ленина, 1");
                p2 = pharmacy(pharmacyRepository, "Аптека №2 Северная", "Муром, ул. Московская, 10");

                user(userRepository, "pharm1", "pharm123", "Иванова А.С.", pharmacist, p1, passwordEncoder);
                user(userRepository, "manager1", "mgr123", "Петров В.И.", manager, p1, passwordEncoder);
                user(userRepository, "admin", "admin123", "Системный администратор",
                        rolesRepository.findAll().stream()
                                .filter(r -> RoleName.ADMIN.equals(r.getName())).findFirst().orElseThrow(),
                        null, passwordEncoder);
                user(userRepository, "accountant", "acc123", "Сидорова О.П.",
                        rolesRepository.findAll().stream()
                                .filter(r -> RoleName.ACCOUNTANT.equals(r.getName())).findFirst().orElseThrow(),
                        p1, passwordEncoder);
            } else {
                List<Pharmacy> pharmacies = pharmacyRepository.findAll();
                if (pharmacies.size() < 2) {
                    return;
                }
                p1 = pharmacies.get(0);
                p2 = pharmacies.get(1);
            }

            AtcManual atx = new AtcManual();
            atx.setCode("N02BE");
            atx.setName("Парацетамол");
            atx.setLevel(4);
            atx = atcManualRepository.save(atx);

            NomenclatureCategory cat = new NomenclatureCategory();
            cat.setName("Обезболивающие");
            cat.setCode("PAIN");
            cat.setSystem(true);
            cat = categoryRepository.save(cat);

            Nomenclature n1 = nomenclature(nomenclatureRepository, atx, cat, "Парацетамол", "Парацетамол",
                    "таб.", 500, "мг", 20, 8900, false, false);
            Nomenclature n2 = nomenclature(nomenclatureRepository, atx, cat, "Нурофен", "Ибупрофен",
                    "таб.", 200, "мг", 10, 35000, false, true);

            NomenclatureCategory hygiene = new NomenclatureCategory();
            hygiene.setName("Гигиена и уход");
            hygiene.setCode("HYG");
            hygiene.setSystem(true);
            hygiene = categoryRepository.save(hygiene);

            Nomenclature n3 = new Nomenclature();
            n3.setProductType(ProductType.COSMETIC.name());
            n3.setBrandName("Бинт стерильный 5 м");
            n3.setFormOfRelease("PACK");
            n3.setQuantityInPack(1);
            n3.setManufacturer("Вершина");
            n3.setCountry("Россия");
            n3.setBarcode("4601234567890");
            n3.setPrice(12000);
            n3.setMinStockLevel(5);
            n3.setMarked(false);
            n3.setReceipt(false);
            n3.setNomenclatureCategories(List.of(hygiene));
            n3 = nomenclatureRepository.save(n3);

            Batch b1 = batch(batchRepository, n1, "BATCH-001", LocalDate.now().plusMonths(8), 100, "ФармПоставка");
            Batch b2 = batch(batchRepository, n2, "BATCH-002", LocalDate.now().plusMonths(6), 50, "ФармПоставка");
            Batch b3 = batch(batchRepository, n3, "BATCH-HYG-001", LocalDate.now().plusMonths(12), 30, "МедСнаб");

            stock(stockRepository, p1, b1, 80, 0);
            stock(stockRepository, p1, b2, 40, 0);
            stock(stockRepository, p2, b1, 20, 0);
            stock(stockRepository, p1, b3, 25, 0);

            MarkingCode mc = new MarkingCode();
            mc.setCode("010460123456789021ABC123");
            mc.setGtin("04601234567890");
            mc.setSerialNumber("ABC123");
            mc.setExpiryDate(b2.getExpiryDate());
            mc.setStatus(MarkingCodeStatus.IN_STOCK);
            mc.setMdlpStatus("REGISTERED");
            mc.setBatch(b2);
            markingCodeRepository.save(mc);
        };
    }

    private static void ensureLeadershipUsers(RolesRepository rolesRepository,
                                              UserRepository userRepository,
                                              PharmacyRepository pharmacyRepository,
                                              PasswordEncoder passwordEncoder) {
        Roles director = rolesRepository.findByName(RoleName.DIRECTOR).orElseThrow();
        Roles owner = rolesRepository.findByName(RoleName.NETWORK_OWNER).orElseThrow();
        Pharmacy pharmacy = pharmacyRepository.findByActiveTrue().stream()
                .findFirst()
                .orElseGet(() -> pharmacy(pharmacyRepository, "Аптека №1 Центральная", "Муром, ул. Ленина, 1"));
        if (!userRepository.existsByLogin("director")) {
            user(userRepository, "director", "dir123", "Директор сети", director, pharmacy, passwordEncoder);
        }
        if (!userRepository.existsByLogin("owner")) {
            user(userRepository, "owner", "owner123", "Владелец аптечной сети", owner, null, passwordEncoder);
        }
    }

    private static Roles role(RolesRepository repo, String name, String desc) {
        Roles r = new Roles();
        r.setName(name);
        r.setDescription(desc);
        return repo.save(r);
    }

    private static Pharmacy pharmacy(PharmacyRepository repo, String name, String street) {
        Pharmacy p = new Pharmacy();
        p.setName(name);
        Address a = new Address();
        a.setStreet(street);
        a.setCity("Муром");
        p.setAddress(a);
        p.setNumberLicense(100001);
        p.setPhoneNumber("+78334560001");
        p.setEmail("apteka@example.ru");
        p.setActive(true);
        return repo.save(p);
    }

    private static void user(UserRepository repo, String login, String pass, String name,
                             Roles role, Pharmacy pharmacy, PasswordEncoder encoder) {
        User u = new User();
        u.setLogin(login);
        u.setPassword(encoder.encode(pass));
        u.setName(name);
        u.setRole(role);
        u.setPharmacy(pharmacy);
        repo.save(u);
    }

    private static Nomenclature nomenclature(NomenclatureRepository repo, AtcManual atx,
                                             NomenclatureCategory cat, String brand, String mnn,
                                             String form, int dosage, String unit, int pack,
                                             int price, boolean receipt, boolean marked) {
        Nomenclature n = new Nomenclature();
        n.setProductType(ProductType.MEDICINE.name());
        n.setAtcManual(atx);
        n.setNomenclatureCategories(List.of(cat));
        n.setBrandName(brand);
        n.setMnn(mnn);
        n.setFormOfRelease(form);
        n.setDosage(dosage);
        n.setDosageUnit(unit);
        n.setQuantityInPack(pack);
        n.setManufacturer("ООО Фарм");
        n.setCountry("Россия");
        n.setBarcode("460" + System.nanoTime() % 1000000000L);
        n.setPrice(price);
        n.setMinStockLevel(10);
        n.setReceipt(receipt);
        n.setNarcotic(false);
        n.setPsychotropic(false);
        n.setMarked(marked);
        return repo.save(n);
    }

    private static Batch batch(BatchRepository repo, Nomenclature n, String num, LocalDate exp, int qty, String supplier) {
        Batch b = new Batch();
        b.setNomenclature(n);
        b.setBatchNumber(num);
        b.setExpiryDate(exp);
        b.setProductionDate(LocalDate.now().minusMonths(2));
        b.setReceivedDate(LocalDate.now());
        b.setSupplier(supplier);
        b.setPrice(n.getPrice());
        b.setQtyReceived(qty);
        b.setQtyInStock(qty);
        b.setStorageZone("Зал");
        b.setWrittenOff(false);
        return repo.save(b);
    }

    private static void ensureTestUser(RolesRepository rolesRepository,
                                       UserRepository userRepository,
                                       PharmacyRepository pharmacyRepository,
                                       PasswordEncoder passwordEncoder) {
        Roles testRole = rolesRepository.findAll().stream()
                .filter(r -> RoleName.TEST.equals(r.getName()))
                .findFirst()
                .orElseGet(() -> role(rolesRepository, RoleName.TEST, "Тест: все разделы"));
        if (userRepository.existsByLogin("test")) {
            return;
        }
        Pharmacy pharmacy = pharmacyRepository.findByActiveTrue().stream()
                .findFirst()
                .orElseGet(() -> pharmacy(pharmacyRepository, "Аптека №1 Центральная", "Муром, ул. Ленина, 1"));
        user(userRepository, "test", "test123", "Тестовый пользователь (все разделы)", testRole, pharmacy, passwordEncoder);
    }

    private static void stock(StockRepository repo, Pharmacy p, Batch b, int qty, int reserved) {
        Stock s = new Stock();
        s.setPharmacy(p);
        s.setBatch(b);
        s.setQuantity(qty);
        s.setReserved(reserved);
        repo.save(s);
    }
}
