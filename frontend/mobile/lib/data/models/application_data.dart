// Since the backend returns a direct array, we'll create a simple wrapper
class ApplicationResponse {
  final int statusCode;
  final String message;
  final List<ApplicationContent> content;

  ApplicationResponse({
    required this.statusCode,
    required this.message,
    required this.content,
  });

  factory ApplicationResponse.fromJson(Map<String, dynamic> json) {
    return ApplicationResponse(
      statusCode: json['statusCode'],
      message: json['message'],
      content:
          (json['data'] as List)
              .map((e) => ApplicationContent.fromJson(e))
              .toList(),
    );
  }
}

class ApplicationContent {
  final String id;
  final String name;
  final String description;
  final String layout;
  final List<ApplicationSection> sections;

  ApplicationContent({
    required this.id,
    required this.name,
    required this.description,
    required this.layout,
    required this.sections,
  });

  factory ApplicationContent.fromJson(Map<String, dynamic> json) {
    return ApplicationContent(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      layout: json['layout'],
      sections:
          (json['sections'] as List)
              .map((e) => ApplicationSection.fromJson(e))
              .toList(),
    );
  }

  // Add this getter to return all fields from all sections
  List<ApplicationField> get fields =>
      sections.expand((section) => section.fields).toList();

  // Add this getter for display name
  String get displayName => name;
}

class ApplicationSection {
  final String id;
  final String title;
  final List<ApplicationField> fields;

  ApplicationSection({
    required this.id,
    required this.title,
    required this.fields,
  });

  factory ApplicationSection.fromJson(Map<String, dynamic> json) {
    return ApplicationSection(
      id: json['id'],
      title: json['title'],
      fields:
          (json['fields'] as List)
              .map((e) => ApplicationField.fromJson(e))
              .toList(),
    );
  }
}

class ApplicationField {
  final String id;
  final String key;
  final String fieldName;
  final String fieldType;
  final bool required;
  final String? defaultValue;
  final List<String>? choices;
  final String? validationRegex;
  final String note;

  ApplicationField({
    required this.id,
    required this.key,
    required this.fieldName,
    required this.fieldType,
    required this.required,
    this.defaultValue,
    this.choices,
    this.validationRegex,
    this.note = '',
  });

  factory ApplicationField.fromJson(Map<String, dynamic> json) {
    return ApplicationField(
      id: json['id'],
      key: json['key'],
      fieldName: json['fieldName'],
      fieldType: json['fieldType'],
      required: json['required'],
      defaultValue: json['defaultValue'],
      choices: (json['choices'] as List?)?.map((e) => e.toString()).toList(),
      validationRegex: json['validationRegex'],
      note: json['note'] ?? '',
    );
  }

  // Add this getter for display name
  String get displayName => fieldName;

  // Add this getter
  bool get hasCoordinate =>
      fieldType.toUpperCase() == 'COORDINATE' || key == 'coordinate';
}
