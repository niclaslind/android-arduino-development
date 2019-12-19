#define buzzer 4

uint8_t patternNumber = 0;
bool command_received = false;

void setup() {
  // put your setup code here, to run once:
  DDRD = DDRD | B11111100;
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:

  // changes pattern number if any serial data has recived 
  switch (patternNumber)
  {
    case 0:
      turnAllOff();
      break;
    case 1:
      turnOnLed();
      break;
    case 2:
      flashLed();
      break;
    case 3:
      twoLedFlash();
      break;
    case 4:
      startBuzzer();
      break;
    case 5:
      stopBuzzer();
      break;
    default:
      break;
  }
}

// handles the recived data on serial port, if any
void serialEvent()
{
  while (command_received == false && Serial.available()) {
    // get the new byte
    char inChar = (char)Serial.read();

    if (inChar == '0') {
      PORTD = B00000000;
      setPatternNumber(0);
      Serial.print(inChar);
      command_received = true;
    } else if (inChar == '1') {
      PORTD = B00000000;
      setPatternNumber(1);
      Serial.print(inChar);
      command_received = true;
    } else if (inChar == '2') {
      PORTD = B00000000;
      setPatternNumber(2);
      Serial.print(inChar);
      command_received = true;
    } else if (inChar == '3') {
      PORTD = B00000000;
      setPatternNumber(3);
      Serial.print(inChar);
      command_received = true;
    } else if (inChar == '4') {
      PORTD = B00000000;
      setPatternNumber(4);
      Serial.print(inChar);
      command_received = true;
    } else if (inChar == '5') {
      PORTD = B00000000;
      setPatternNumber(5);
      Serial.print(inChar);
      command_received = true;
    }
  }

  command_received = false;
}

void setPatternNumber(uint8_t newPattern)
{
  patternNumber = newPattern;
}

void turnAllOff()
{
  PORTD = B00000000;
  stopBuzzer();
}

/********************************* First task, turn on a LED   *********************************/
void turnOnLed()
{
  // turns on LED connected to pin 2
  PORTD = B00000100;
}

/********************************* Second task, make a LED flash  *********************************/
void flashLed()
{
  // variabel for delay time between flash
  int16_t delayTime = 200;
  // Led connected to pin 2 is on
  PORTD = B00000100;
  delay(delayTime);
  // Led connected to pin 2 is off
  PORTD = B00000000;
  delay(delayTime);
}

/********************************* Third task, make two LED flash asynchronously   *********************************/
void twoLedFlash()
{
  // variabel for delay time between sequence
  int16_t delayTime = 400;
  // Led connected to pin 2 is on and Led connected to pin 3 is off
  PORTD = B00000100;
  delay(delayTime);
  // Led connected to pin 3 is on and Led conncted to pind  2 is off
  PORTD = B00001000;
  delay(delayTime);
}

/********************************* Fourth task, make a buzzer play a tone  *********************************/
void startBuzzer()
{
  tone(buzzer, 1000);
}

// And stop buzzer
void stopBuzzer()
{
  noTone(buzzer);
}

