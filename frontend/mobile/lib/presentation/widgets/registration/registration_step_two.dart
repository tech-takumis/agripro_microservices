import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:mobile/presentation/widgets/common/custom_text_field.dart';
import 'package:mobile/presentation/widgets/common/custom_dropdown.dart';
import 'package:mobile/data/models/psgc_models.dart';
import 'package:mobile/data/services/psgc_service.dart';
import 'package:mobile/injection_container.dart';

/// Second step of registration: Geographic Information
class RegistrationStepTwo extends StatefulWidget {
  final GlobalKey<FormState> formKey;
  final TextEditingController zipCodeController;
  final TextEditingController streetController;
  final TextEditingController barangayController;

  const RegistrationStepTwo({
    super.key,
    required this.formKey,
    required this.zipCodeController,
    required this.streetController,
    required this.barangayController,
  });

  @override
  State<RegistrationStepTwo> createState() => RegistrationStepTwoState();
}

class RegistrationStepTwoState extends State<RegistrationStepTwo> with AutomaticKeepAliveClientMixin {
  final PSGCService _psgcService = getIt<PSGCService>();

  // Geographic data
  List<PSGCRegion> _regions = [];
  List<PSGCProvince> _provinces = [];
  List<PSGCCity> _cities = [];

  // Selected values
  PSGCRegion? _selectedRegion;
  PSGCProvince? _selectedProvince;
  PSGCCity? _selectedCity;

  // Loading states
  bool _loadingRegions = false;
  bool _loadingProvinces = false;
  bool _loadingCities = false;

  @override
  void initState() {
    super.initState();
    _loadRegions();
  }

  Future<void> _loadRegions() async {
    setState(() => _loadingRegions = true);
    try {
      final regions = await _psgcService.getRegions();
      setState(() {
        _regions = regions;
        _loadingRegions = false;
      });
    } catch (e) {
      setState(() => _loadingRegions = false);
      Get.snackbar(
        'Error',
        'Failed to load regions: ${e.toString()}',
        snackPosition: SnackPosition.BOTTOM,
        backgroundColor: Colors.red[100],
        colorText: Colors.red[800],
      );
    }
  }

  Future<void> _loadProvinces(String regionCode) async {
    setState(() {
      _loadingProvinces = true;
      _provinces = [];
      _cities = [];
      _selectedProvince = null;
      _selectedCity = null;
    });

    try {
      final provinces = await _psgcService.getProvincesByRegion(regionCode);
      setState(() {
        _provinces = provinces;
        _loadingProvinces = false;
      });
    } catch (e) {
      setState(() => _loadingProvinces = false);
      Get.snackbar(
        'Error',
        'Failed to load provinces: ${e.toString()}',
        snackPosition: SnackPosition.BOTTOM,
        backgroundColor: Colors.red[100],
        colorText: Colors.red[800],
      );
    }
  }

  Future<void> _loadCities(String provinceCode) async {
    setState(() {
      _loadingCities = true;
      _cities = [];
      _selectedCity = null;
    });

    try {
      final cities = await _psgcService.getCitiesByProvince(provinceCode);
      setState(() {
        _cities = cities;
        _loadingCities = false;
      });
    } catch (e) {
      setState(() => _loadingCities = false);
      Get.snackbar(
        'Error',
        'Failed to load cities: ${e.toString()}',
        snackPosition: SnackPosition.BOTTOM,
        backgroundColor: Colors.red[100],
        colorText: Colors.red[800],
      );
    }
  }

  // Getters for selected values (to be used by parent widget)
  String get selectedRegion => _selectedRegion?.regionName ?? '';
  String get selectedProvince => _selectedProvince?.name ?? '';
  String get selectedCity => _selectedCity?.name ?? '';

  @override
  Widget build(BuildContext context) {
    super.build(context); // Important for AutomaticKeepAliveClientMixin
    return Form(
      key: widget.formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Header
          Text(
            'Geographic Information',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
              fontWeight: FontWeight.bold,
              color: Colors.grey[800],
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Please select your location details',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: Colors.grey[600],
            ),
          ),
          const SizedBox(height: 32),

          // Region Dropdown
          CustomDropdown<PSGCRegion>(
            label: 'Region *',
            value: _selectedRegion,
            items: _regions,
            displayText: (region) => region.regionName,
            isLoading: _loadingRegions,
            hint: 'Select your region',
            prefixIcon: Icons.location_on_outlined,
            onChanged: (region) {
              setState(() => _selectedRegion = region);
              if (region != null) {
                _loadProvinces(region.code);
              }
            },
            validator: (value) {
              if (value == null) {
                return 'Please select your region';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Province Dropdown
          CustomDropdown<PSGCProvince>(
            label: 'Province *',
            value: _selectedProvince,
            items: _provinces,
            displayText: (province) => province.name,
            isLoading: _loadingProvinces,
            hint: _selectedRegion == null
                ? 'Select region first'
                : 'Select your province',
            prefixIcon: Icons.map_outlined,
            onChanged: _selectedRegion == null ? null : (province) {
              setState(() => _selectedProvince = province);
              if (province != null) {
                _loadCities(province.code);
              }
            },
            validator: (value) {
              if (value == null) {
                return 'Please select your province';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // City/Municipality Dropdown
          CustomDropdown<PSGCCity>(
            label: 'City/Municipality *',
            value: _selectedCity,
            items: _cities,
            displayText: (city) => city.name,
            isLoading: _loadingCities,
            hint: _selectedProvince == null
                ? 'Select province first'
                : 'Select your city/municipality',
            prefixIcon: Icons.location_city_outlined,
            onChanged: _selectedProvince == null ? null : (city) {
              setState(() => _selectedCity = city);
            },
            validator: (value) {
              if (value == null) {
                return 'Please select your city/municipality';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Barangay Dropdown (PSGC) - only enabled after city selection
          _selectedCity == null
              ? CustomDropdown<PSGCBarangay>(
                  label: 'Barangay *',
                  value: null,
                  items: [],
                  isLoading: false,
                  hint: 'Select city/municipality first',
                  prefixIcon: Icons.location_searching,
                  displayText: (barangay) => barangay.name,
                  onChanged: null,
                )
              : FutureBuilder<List<PSGCBarangay>>(
                  future: _psgcService.getBarangaysByCity(_selectedCity!.code),
                  builder: (context, snapshot) {
                    if (snapshot.connectionState == ConnectionState.waiting) {
                      return CustomDropdown<PSGCBarangay>(
                        label: 'Barangay *',
                        value: null,
                        items: [],
                        isLoading: true,
                        hint: 'Loading barangays...',
                        prefixIcon: Icons.location_searching,
                        displayText: (barangay) => barangay.name,
                        onChanged: null,
                      );
                    } else if (snapshot.hasError) {
                      return Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          CustomDropdown<PSGCBarangay>(
                            label: 'Barangay *',
                            value: null,
                            items: [],
                            isLoading: false,
                            hint: 'Error loading barangays',
                            prefixIcon: Icons.location_searching,
                            displayText: (barangay) => barangay.name,
                            onChanged: null,
                          ),
                          TextButton(
                            onPressed: () => setState(() {}),
                            child: const Text('Retry'),
                          ),
                        ],
                      );
                    } else {
                      final barangays = snapshot.data ?? [];
                      PSGCBarangay? selectedBarangay;
                      if (widget.barangayController.text.isNotEmpty) {
                        try {
                          selectedBarangay = barangays.firstWhere(
                            (b) => b.name == widget.barangayController.text,
                          );
                        } catch (e) {
                          selectedBarangay = null;
                        }
                      }
                      return CustomDropdown<PSGCBarangay>(
                        label: 'Barangay *',
                        value: selectedBarangay,
                        items: barangays,
                        displayText: (barangay) => barangay.name,
                        isLoading: false,
                        hint: barangays.isEmpty
                            ? 'No barangays found for this city'
                            : 'Select your barangay',
                        prefixIcon: Icons.location_searching,
                        onChanged: (barangay) {
                          widget.barangayController.text = barangay?.name ?? '';
                        },
                        validator: (value) {
                          if (value == null) {
                            return 'Please select your barangay';
                          }
                          return null;
                        },
                      );
                    }
                  },
                ),
          const SizedBox(height: 20),

          // ZIP Code
          CustomTextField(
            controller: widget.zipCodeController,
            label: 'ZIP Code *',
            prefixIcon: Icons.local_post_office_outlined,
            keyboardType: TextInputType.number,
            inputFormatters: [
              FilteringTextInputFormatter.digitsOnly,
              LengthLimitingTextInputFormatter(4),
            ],
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Please enter your ZIP code';
              }
              if (value.trim().length != 4) {
                return 'ZIP code must be 4 digits';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Street
          CustomTextField(
            controller: widget.streetController,
            label: 'Street *',
            prefixIcon: Icons.home_outlined,
            keyboardType: TextInputType.text,
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Please enter your street';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),
        ],
      ),
    );
  }

  @override
  bool get wantKeepAlive => true;
}
