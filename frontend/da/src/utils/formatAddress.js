import { usePsgcStore } from '@/stores/psgc'

export const formatAddress = (location) => {
    if (!location) return '';

    const psgcStore = usePsgcStore()
    const parts = [];

    // Helper function to get name from code
    const getName = (code, list) => {
        if (!code) return '';
        const item = list.find(item => item.code === code);
        return item ? item.name : code; // Fallback to code if name not found
    };

    if (location.barangay) {
        const barangayName = getName(location.barangay, psgcStore.getBarangays);
        parts.push(`Bry. ${barangayName}`);
    }
    if (location.municipality) {
        const municipalityName = getName(location.municipality, psgcStore.getMunicipalities);
        parts.push(municipalityName);
    }
    if (location.province) {
        const provinceName = getName(location.province, psgcStore.getProvinces);
        parts.push(provinceName);
    }
    if (location.region) {
        const regionName = getName(location.region, psgcStore.getRegions);
        parts.push(regionName);
    }

    return parts.join(', ');
};

