package com.hashjosh.rsbsa.runner;

import com.hashjosh.rsbsa.entity.Rsbsa;
import com.hashjosh.rsbsa.repository.RsbsaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RsbsaIInitializer implements CommandLineRunner {
    private final RsbsaRepository rsbsaRepository;

    private boolean isRsbsaRecordsNotNull() {
        return rsbsaRepository.count() > 0;
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void run(String... args) throws Exception {
        if (!isRsbsaRecordsNotNull()) {
            List<Rsbsa> records = new ArrayList<>();

            // Record 1: Rice Farmer in Nueva Ecija
            records.add(Rsbsa.builder()
                    .rsbsaId("RSBSA-2025-001")
                    .lastName("Dela Cruz")
                    .firstName("Juan")
                    .middleName("Santos")
                    .dateOfBirth(LocalDate.of(1978, 5, 15))
                    .gender("Male")
                    .civilStatus("Married")
                    .address("Purok 5")
                    .barangay("San Isidro")
                    .municipality("Cabanatuan City")
                    .province("Nueva Ecija")
                    .contactNumber("09123456789")
                    .email("juandelacruz@gmail.com")
                    .farmingType("Crop Farming")
                    .primaryCrop("Rice")
                    .secondaryCrop("Corn")
                    .farmArea(2.5f)
                    .farmLocation("Purok 5, San Isidro, Cabanatuan City, Nueva Ecija")
                    .tenureStatus("Owner")
                    .sourceOfIncome("Farming")
                    .estimatedIncome(120000.00f)
                    .householdSize(5)
                    .educationLevel("High School")
                    .withDisability(false)
                    .build());

            // Record 2: Corn Farmer in Isabela
            records.add(Rsbsa.builder()
                    .rsbsaId("RSBSA-2025-002")
                    .lastName("Reyes")
                    .firstName("Maria")
                    .middleName("Gomez")
                    .dateOfBirth(LocalDate.of(1985, 3, 22))
                    .gender("Female")
                    .civilStatus("Single")
                    .address("Sitio Magsaysay")
                    .barangay("San Vicente")
                    .municipality("Ilagan")
                    .province("Isabela")
                    .contactNumber("09234567890")
                    .email("mariareyes@yahoo.com")
                    .farmingType("Crop Farming")
                    .primaryCrop("Corn")
                    .secondaryCrop("Vegetables")
                    .farmArea(3.0f)
                    .farmLocation("Sitio Magsaysay, San Vicente, Ilagan, Isabela")
                    .tenureStatus("Tenant")
                    .sourceOfIncome("Farming")
                    .estimatedIncome(100000.00f)
                    .householdSize(4)
                    .educationLevel("Elementary")
                    .withDisability(false)
                    .build());

            // Record 3: Coconut Farmer in Quezon
            records.add(Rsbsa.builder()
                    .rsbsaId("RSBSA-2025-003")
                    .lastName("Santos")
                    .firstName("Pedro")
                    .middleName("Lopez")
                    .dateOfBirth(LocalDate.of(1965, 11, 10))
                    .gender("Male")
                    .civilStatus("Widower")
                    .address("Purok 3")
                    .barangay("Maligaya")
                    .municipality("Lucena City")
                    .province("Quezon")
                    .contactNumber("09345678901")
                    .email("")
                    .farmingType("Crop Farming")
                    .primaryCrop("Coconut")
                    .secondaryCrop("Banana")
                    .farmArea(4.0f)
                    .farmLocation("Purok 3, Maligaya, Lucena City, Quezon")
                    .tenureStatus("Owner")
                    .sourceOfIncome("Farming")
                    .estimatedIncome(150000.00f)
                    .householdSize(6)
                    .educationLevel("High School")
                    .withDisability(true)
                    .build());

            // Record 4: Vegetable Farmer in Benguet
            records.add(Rsbsa.builder()
                    .rsbsaId("RSBSA-2025-004")
                    .lastName("Fernandez")
                    .firstName("Ana")
                    .middleName("Cruz")
                    .dateOfBirth(LocalDate.of(1990, 7, 8))
                    .gender("Female")
                    .civilStatus("Married")
                    .address("Km 5")
                    .barangay("La Trinidad")
                    .municipality("La Trinidad")
                    .province("Benguet")
                    .contactNumber("09456789012")
                    .email("anafernandez@gmail.com")
                    .farmingType("Crop Farming")
                    .primaryCrop("Vegetables")
                    .secondaryCrop("Potato")
                    .farmArea(1.5f)
                    .farmLocation("Km 5, La Trinidad, Benguet")
                    .tenureStatus("Lessee")
                    .sourceOfIncome("Farming")
                    .estimatedIncome(80000.00f)
                    .householdSize(3)
                    .educationLevel("College")
                    .withDisability(false)
                    .build());

            // Record 5: Rice Farmer in Pangasinan
            records.add(Rsbsa.builder()
                    .rsbsaId("RSBSA-2025-005")
                    .lastName("Garcia")
                    .firstName("Jose")
                    .middleName("Ramos")
                    .dateOfBirth(LocalDate.of(1970, 2, 14))
                    .gender("Male")
                    .civilStatus("Married")
                    .address("Purok 7")
                    .barangay("Poblacion")
                    .municipality("Urdaneta City")
                    .province("Pangasinan")
                    .contactNumber("09567890123")
                    .email("")
                    .farmingType("Crop Farming")
                    .primaryCrop("Rice")
                    .secondaryCrop("Mango")
                    .farmArea(2.0f)
                    .farmLocation("Purok 7, Poblacion, Urdaneta City, Pangasinan")
                    .tenureStatus("Owner")
                    .sourceOfIncome("Farming")
                    .estimatedIncome(110000.00f)
                    .householdSize(5)
                    .educationLevel("High School")
                    .withDisability(false)
                    .build());

            // Record 6: Sugarcane Farmer in Negros Occidental
            records.add(Rsbsa.builder()
                    .rsbsaId("RSBSA-2025-006")
                    .lastName("Lopez")
                    .firstName("Carmen")
                    .middleName("Tan")
                    .dateOfBirth(LocalDate.of(1982, 9, 30))
                    .gender("Female")
                    .civilStatus("Single")
                    .address("Hacienda San Miguel")
                    .barangay("Bago")
                    .municipality("Bago City")
                    .province("Negros Occidental")
                    .contactNumber("09678901234")
                    .email("carmenlopez@gmail.com")
                    .farmingType("Crop Farming")
                    .primaryCrop("Sugarcane")
                    .secondaryCrop("Corn")
                    .farmArea(5.0f)
                    .farmLocation("Hacienda San Miguel, Bago, Bago City, Negros Occidental")
                    .tenureStatus("Tenant")
                    .sourceOfIncome("Farming")
                    .estimatedIncome(200000.00f)
                    .householdSize(4)
                    .educationLevel("Elementary")
                    .withDisability(false)
                    .build());

            // Record 7: Rice Farmer in Agusan del Sur
            records.add(Rsbsa.builder()
                    .rsbsaId("RSBSA-2025-007")
                    .lastName("Tan")
                    .firstName("Miguel")
                    .middleName("Perez")
                    .dateOfBirth(LocalDate.of(1975, 12, 5))
                    .gender("Male")
                    .civilStatus("Married")
                    .address("Purok 2")
                    .barangay("Charito")
                    .municipality("Bayugan City")
                    .province("Agusan del Sur")
                    .contactNumber("09789012345")
                    .email("")
                    .farmingType("Crop Farming")
                    .primaryCrop("Rice")
                    .secondaryCrop("Vegetables")
                    .farmArea(3.5f)
                    .farmLocation("Purok 2, Charito, Bayugan City, Agusan del Sur")
                    .tenureStatus("Owner")
                    .sourceOfIncome("Farming")
                    .estimatedIncome(130000.00f)
                    .householdSize(6)
                    .educationLevel("High School")
                    .withDisability(true)
                    .build());

            // Record 8: Banana Farmer in Davao del Norte
            records.add(Rsbsa.builder()
                    .rsbsaId("RSBSA-2025-008")
                    .lastName("Cruz")
                    .firstName("Sofia")
                    .middleName("Diaz")
                    .dateOfBirth(LocalDate.of(1988, 4, 20))
                    .gender("Female")
                    .civilStatus("Married")
                    .address("Sitio Tagum")
                    .barangay("Mankilam")
                    .municipality("Tagum City")
                    .province("Davao del Norte")
                    .contactNumber("09890123456")
                    .email("sofiacruz@gmail.com")
                    .farmingType("Crop Farming")
                    .primaryCrop("Banana")
                    .secondaryCrop("Coconut")
                    .farmArea(4.5f)
                    .farmLocation("Sitio Tagum, Mankilam, Tagum City, Davao del Norte")
                    .tenureStatus("Lessee")
                    .sourceOfIncome("Farming")
                    .estimatedIncome(180000.00f)
                    .householdSize(5)
                    .educationLevel("College")
                    .withDisability(false)
                    .build());

            // Record 9: Rice Farmer in Tarlac
            records.add(Rsbsa.builder()
                    .rsbsaId("RSBSA-2025-009")
                    .lastName("Ramirez")
                    .firstName("Roberto")
                    .middleName("Gomez")
                    .dateOfBirth(LocalDate.of(1968, 6, 12))
                    .gender("Male")
                    .civilStatus("Widower")
                    .address("Purok 4")
                    .barangay("San Manuel")
                    .municipality("Tarlac City")
                    .province("Tarlac")
                    .contactNumber("09901234567")
                    .email("")
                    .farmingType("Crop Farming")
                    .primaryCrop("Rice")
                    .secondaryCrop("Onion")
                    .farmArea(2.8f)
                    .farmLocation("Purok 4, San Manuel, Tarlac City, Tarlac")
                    .tenureStatus("Owner")
                    .sourceOfIncome("Farming")
                    .estimatedIncome(125000.00f)
                    .householdSize(4)
                    .educationLevel("High School")
                    .withDisability(false)
                    .build());

            // Record 10: Vegetable Farmer in Cavite
            records.add(Rsbsa.builder()
                    .rsbsaId("RSBSA-2025-010")
                    .lastName("Morales")
                    .firstName("Luz")
                    .middleName("Villar")
                    .dateOfBirth(LocalDate.of(1992, 8, 25))
                    .gender("Female")
                    .civilStatus("Single")
                    .address("Purok 6")
                    .barangay("Kaytitinga")
                    .municipality("Alfonso")
                    .province("Cavite")
                    .contactNumber("09012345678")
                    .email("luzmorales@gmail.com")
                    .farmingType("Crop Farming")
                    .primaryCrop("Vegetables")
                    .secondaryCrop("Tomato")
                    .farmArea(1.2f)
                    .farmLocation("Purok 6, Kaytitinga, Alfonso, Cavite")
                    .tenureStatus("Tenant")
                    .sourceOfIncome("Farming")
                    .estimatedIncome(90000.00f)
                    .householdSize(3)
                    .educationLevel("College")
                    .withDisability(false)
                    .build());

            rsbsaRepository.saveAll(records);
            System.out.println("✅ Inserted " + records.size() + " real RSBSA records.");
        } else {
            System.out.println("✅ RSBSA records already exist. Skipping initialization.");
        }
    }

}
