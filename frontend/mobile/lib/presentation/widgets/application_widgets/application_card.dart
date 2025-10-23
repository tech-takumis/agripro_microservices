import 'package:flutter/material.dart';
import '../../../data/models/application_data.dart';

class ApplicationCard extends StatelessWidget {
  final ApplicationContent application;
  final VoidCallback onTap;

  const ApplicationCard({
    super.key,
    required this.application,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    return Card(
      elevation: 2,
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Application name
              Row(
                children: [
                  Icon(
                    Icons.description_outlined,
                    color: Colors.green[700],
                    size: 24,
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Text(
                      application.name,
                      style: theme.textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.w600,
                        color: Colors.green[700],
                      ),
                    ),
                  ),
                  Icon(
                    Icons.arrow_forward_ios,
                    size: 16,
                    color: const Color.fromARGB(255, 235, 234, 234),
                  ),
                ],
              ),
              
              const SizedBox(height: 12),
              
              // Application description
              Text(
                application.description,
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: const Color.fromARGB(255, 0, 0, 0),
                  height: 1.4,
                ),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              
              const SizedBox(height: 16),
              
              // Application metadata
              Row(
                children: [
                  _buildInfoChip(
                    icon: Icons.layers_outlined,
                    label: '${application.sections.length} Sections',
                    color: const Color.fromARGB(255, 218, 17, 17),
                  ),
                  const SizedBox(width: 8),
                  _buildInfoChip(
                    icon: Icons.edit_note,
                    label: '${_getTotalFields()} Fields',
                    color: Colors.green,
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildInfoChip({
    required IconData icon,
    required String label,
    required Color color,
  }) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 14, color: color),
          const SizedBox(width: 4),
          Text(
            label,
            style: TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.w500,
              color: color,
            ),
          ),
        ],
      ),
    );
  }

  int _getTotalFields() {
    return application.sections.fold(
      0,
      (sum, section) => sum + section.fields.length,
    );
  }
}
