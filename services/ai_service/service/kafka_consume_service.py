import json
import threading
from confluent_kafka import Consumer
from config.kafka_config import KAFKA_CONFIG, TOPIC_NAME
from sqlalchemy.orm import Session
from config.database import get_db
from models.ai_result import AIResult


def consume_application_submit_event():
    consumer = Consumer(KAFKA_CONFIG)
    consumer.subscribe([TOPIC_NAME])
    print(f"Kafka listening to topic: {TOPIC_NAME}")

    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                continue
            if msg.error():
                print(f"[Kafka] Error: {msg.error()}")
                continue

            data = msg.value().decode('utf-8')
            print(f"[Kafka] Received message: {data}")
            try:
                payload = json.loads(data)
                print(f"[Kafka] Received data: {payload}")
            except json.JSONDecodeError:
                print("[Kafka] Invalid JSON data")
                payload = {"raw": data}
            handle_message(payload)
    except KeyboardInterrupt:
        print("[Kafka] Consumer stopped manually.")
    finally:
        consumer.close()

def handle_message(message):
    """Process each message here."""
    print(f"[Kafka] Received message: {message}")
    if isinstance(message, dict) and message.get("provider") == "PCIC":
        db_gen = get_db()
        db = next(db_gen)
        ai_result = AIResult(
            applicationId=message.get("submissionId"),
            result="mock_result",
            prediction="mock_prediction",
            accuracy="mock_accuracy"
        )
        db.add(ai_result)
        db.commit()
        db.refresh(ai_result)
        print(f"[Kafka] Saved AIResult for PCIC: {ai_result}")


def start_consumer_in_thread():
    thread = threading.Thread(target=consume_application_submit_event,daemon=True)
    thread.start()
    print("[Kafka] Consumer thread started.")