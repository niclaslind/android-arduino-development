void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  // sets the ports 2-7 to outputs
  DDRD = DDRD | B11111100;
}

void loop() {
  // put your main code here, to run repeatedly:
  turnOnLed();
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

/********************************* Fourth task, make a binarycounter from 0-15   *********************************/
void binaryCounter()
{
  // array of values 
  int binaryCounterValues[] = {
    B00000000,    //0
    B00000100,    //1
    B00001000,    //2
    B00001100,    //3
    B00010000,    //4
    B00010100,    //5
    B00011000,    //6
    B00011100,    //7
    B00100000,    //8
    B00100100,    //9
    B00101000,    //10
    B00101100,    //11
    B00110000,    //12
    B00110100,    //13
    B00111000,    //14
    B00111100     //15
  };
  // size of array named binaryCounterValues
  uint16_t sizeOfBinaryCounter = sizeof(binaryCounterValues) / sizeof(int);
  
  // variabel for delay time between sequence
  uint16_t delayTime = 500;

  // goes through the the array and then start over again
  for (int i = 0; i < sizeOfBinaryCounter; i++)
  {
    PORTD = binaryCounterValues[i];
    delay(delayTime);
  }
}

/********************************* Fifth task, make a knightrider sequence   *********************************/
void knightRider()
{
  // array of knightridersequences
  int nightRiderSequences[] = {
    B00000100,
    B00001100,
    B00001000,
    B00011000,
    B00010000,
    B00110000,
    B00100000
  };

  // size of array
  uint16_t nightRiderSize = sizeof(nightRiderSequences) / sizeof(int);

  // variabel for delay time between sequence
  uint16_t delayTime = 60;

  // goes through the array
  for (int i = 0; i < nightRiderSize; i++) {
    PORTD = nightRiderSequences[i];
    delay(delayTime);
  }

  // goes through the array backwards
  for (int i = nightRiderSize - 1; i > 0; i--) {
    PORTD = nightRiderSequences[i];
    delay(delayTime);
  }
}

