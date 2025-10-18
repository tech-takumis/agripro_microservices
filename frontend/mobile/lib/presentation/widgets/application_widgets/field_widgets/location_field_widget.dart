import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:mobile/data/models/psgc_models.dart';
import 'package:mobile/data/services/psgc_service.dart';

import '../../../../injection_container.dart';

/// Widget for location selection using PSGC
class LocationFieldWidget extends ConsumerStatefulWidget {
  final ApplicationField field;
  final Map<String, String?> locationValues;
  final void Function(String key, String? value) onChanged;
  final String? Function(Map<String, String?>)? validator;

  const LocationFieldWidget({
    super.key,
    required this.field,
    required this.locationValues,
    required this.onChanged,
    this.validator,
  });

  @override
  ConsumerState<LocationFieldWidget> createState() => _LocationFieldWidgetState();
}

class _LocationFieldWidgetState extends ConsumerState<LocationFieldWidget> {
  List<PSGCRegion> _regions = [];
  List<PSGCProvince> _provinces = [];
  List<PSGCCity> _cities = [];
  List<PSGCBarangay> _barangays = [];

  bool _isLoadingRegions = true;
  bool _isLoadingProvinces = false;
  bool _isLoadingCities = false;
  bool _isLoadingBarangays = false;

  String? _selectedRegionCode;
  String? _selectedProvinceCode;
  String? _selectedCityCode;
  String? _selectedBarangayCode;

  @override
  void initState() {
    super.initState();
    print('🔵 LocationFieldWidget: initState called');
    _initializeFromValues();
    _loadRegions();
  }

  void _initializeFromValues() {
    _selectedRegionCode = widget.locationValues['region'];
    _selectedProvinceCode = widget.locationValues['province'];
    _selectedCityCode = widget.locationValues['city'];
    _selectedBarangayCode = widget.locationValues['barangay'];
    print('🔵 LocationFieldWidget: Initialized with values: $_selectedRegionCode, $_selectedProvinceCode, $_selectedCityCode, $_selectedBarangayCode');
  }

  Future<void> _loadRegions() async {
    try {
      print('🔵 LocationFieldWidget: Loading regions...');
      setState(() => _isLoadingRegions = true);
      final regions = await getIt<PSGCService>().getRegions();
      print('🔵 LocationFieldWidget: Loaded ${regions.length} regions');
      if (mounted) {
        setState(() {
          _regions = regions;
          _isLoadingRegions = false;
        });
      }
    } catch (e, stackTrace) {
      print('🔴 LocationFieldWidget: Failed to load regions: $e');
      print('🔴 StackTrace: $stackTrace');
      if (mounted) {
        setState(() => _isLoadingRegions = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to load regions: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  Future<void> _loadProvinces(String regionCode) async {
    try {
      print('🔵 LocationFieldWidget: Loading provinces for region: $regionCode');
      setState(() {
        _isLoadingProvinces = true;
        _provinces = [];
        _cities = [];
        _barangays = [];
        _selectedProvinceCode = null;
        _selectedCityCode = null;
        _selectedBarangayCode = null;
      });

      final provinces = await getIt<PSGCService>().getProvincesByRegion(regionCode);
      print('🔵 LocationFieldWidget: Loaded ${provinces.length} provinces');
      if (mounted) {
        setState(() {
          _provinces = provinces;
          _isLoadingProvinces = false;
        });
      }
    } catch (e) {
      print('🔴 LocationFieldWidget: Failed to load provinces: $e');
      if (mounted) {
        setState(() => _isLoadingProvinces = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to load provinces: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  Future<void> _loadCities(String provinceCode) async {
    try {
      print('🔵 LocationFieldWidget: Loading cities for province: $provinceCode');
      setState(() {
        _isLoadingCities = true;
        _cities = [];
        _barangays = [];
        _selectedCityCode = null;
        _selectedBarangayCode = null;
      });

      final cities = await getIt<PSGCService>().getCitiesByProvince(provinceCode);
      print('🔵 LocationFieldWidget: Loaded ${cities.length} cities');
      if (mounted) {
        setState(() {
          _cities = cities;
          _isLoadingCities = false;
        });
      }
    } catch (e) {
      print('🔴 LocationFieldWidget: Failed to load cities: $e');
      if (mounted) {
        setState(() => _isLoadingCities = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to load cities: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  Future<void> _loadBarangays(String cityCode) async {
    try {
      print('🔵 LocationFieldWidget: Loading barangays for city: $cityCode');
      setState(() {
        _isLoadingBarangays = true;
        _barangays = [];
        _selectedBarangayCode = null;
      });

      final barangays = await getIt<PSGCService>().getBarangaysByCity(cityCode);
      print('🔵 LocationFieldWidget: Loaded ${barangays.length} barangays');
      if (mounted) {
        setState(() {
          _barangays = barangays;
          _isLoadingBarangays = false;
        });
      }
    } catch (e) {
      print('🔴 LocationFieldWidget: Failed to load barangays: $e');
      if (mounted) {
        setState(() => _isLoadingBarangays = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to load barangays: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final hasError = widget.validator?.call(widget.locationValues) != null;
    print('🔵 LocationFieldWidget: Building with ${_regions.length} regions, loading: $_isLoadingRegions');

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          widget.field.fieldName + (widget.field.required ? ' *' : ''),
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w500,
            color: Colors.grey[800],
          ),
        ),
        const SizedBox(height: 12),

        // Region Dropdown
        _buildDropdown(
          label: 'Region',
          value: _selectedRegionCode,
          items: _regions
              .map((r) => DropdownMenuItem(
                    value: r.code,
                    child: Text(r.regionName),
                  ))
              .toList(),
          isLoading: _isLoadingRegions,
          enabled: !_isLoadingRegions,
          onChanged: (value) {
            print('🔵 LocationFieldWidget: Region selected: $value');
            if (value != null) {
              setState(() => _selectedRegionCode = value);
              final selectedRegion =
                  _regions.firstWhere((r) => r.code == value);
              widget.onChanged('region', selectedRegion.regionName);
              _loadProvinces(value);
            }
          },
        ),

        const SizedBox(height: 12),

        // Province Dropdown
        _buildDropdown(
          label: 'Province',
          value: _selectedProvinceCode,
          items: _provinces
              .map((p) => DropdownMenuItem(
                    value: p.code,
                    child: Text(p.name),
                  ))
              .toList(),
          isLoading: _isLoadingProvinces,
          enabled: _provinces.isNotEmpty && !_isLoadingProvinces,
          onChanged: (value) {
            print('🔵 LocationFieldWidget: Province selected: $value');
            if (value != null) {
              setState(() => _selectedProvinceCode = value);
              final selectedProvince =
                  _provinces.firstWhere((p) => p.code == value);
              widget.onChanged('province', selectedProvince.name);
              _loadCities(value);
            }
          },
        ),

        const SizedBox(height: 12),

        // City/Municipality Dropdown
        _buildDropdown(
          label: 'City/Municipality',
          value: _selectedCityCode,
          items: _cities
              .map((c) => DropdownMenuItem(
                    value: c.code,
                    child: Text(c.name),
                  ))
              .toList(),
          isLoading: _isLoadingCities,
          enabled: _cities.isNotEmpty && !_isLoadingCities,
          onChanged: (value) {
            print('🔵 LocationFieldWidget: City selected: $value');
            if (value != null) {
              setState(() => _selectedCityCode = value);
              final selectedCity = _cities.firstWhere((c) => c.code == value);
              widget.onChanged('city', selectedCity.name);
              _loadBarangays(value);
            }
          },
        ),

        const SizedBox(height: 12),

        // Barangay Dropdown
        _buildDropdown(
          label: 'Barangay',
          value: _selectedBarangayCode,
          items: _barangays
              .map((b) => DropdownMenuItem(
                    value: b.code,
                    child: Text(b.name),
                  ))
              .toList(),
          isLoading: _isLoadingBarangays,
          enabled: _barangays.isNotEmpty && !_isLoadingBarangays,
          onChanged: (value) {
            print('🔵 LocationFieldWidget: Barangay selected: $value');
            if (value != null) {
              setState(() => _selectedBarangayCode = value);
              final selectedBarangay =
                  _barangays.firstWhere((b) => b.code == value);
              widget.onChanged('barangay', selectedBarangay.name);
            }
          },
        ),

        if (hasError)
          Padding(
            padding: const EdgeInsets.only(left: 16, top: 8),
            child: Text(
              widget.validator!.call(widget.locationValues)!,
              style: TextStyle(
                color: Theme.of(context).colorScheme.error,
                fontSize: 12,
              ),
            ),
          ),
      ],
    );
  }

  Widget _buildDropdown({
    required String label,
    required String? value,
    required List<DropdownMenuItem<String>> items,
    required bool isLoading,
    bool enabled = true,
    required void Function(String?) onChanged,
  }) {
    final hasItems = items.isNotEmpty;
    
    return DropdownButtonFormField<String>(
      value: value,
      decoration: InputDecoration(
        labelText: label,
        hintText: !hasItems
            ? (isLoading ? 'Loading...' : 'Please select previous field first')
            : null,
        filled: !enabled,
        fillColor: !enabled ? Colors.grey[100] : null,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
        ),
        contentPadding: const EdgeInsets.symmetric(
          horizontal: 16,
          vertical: 16,
        ),
        suffixIcon: isLoading
            ? const Padding(
                padding: EdgeInsets.all(12),
                child: SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(strokeWidth: 2),
                ),
              )
            : const Icon(Icons.arrow_drop_down),
      ),
      items: hasItems ? items : null,
      onChanged: enabled && hasItems && !isLoading ? onChanged : null,
      isExpanded: true,
    );
  }
}