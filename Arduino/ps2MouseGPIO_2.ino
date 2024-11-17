
#define MDATA 4
#define MCLK 5


void gohi(int pin){
  pinMode(pin, INPUT);
  digitalWrite(pin, HIGH);
}

void golo(int pin){
  pinMode(pin, OUTPUT);
  digitalWrite(pin, LOW);
}

void mouse_write(char data){
  char i;
  char parity = 1;

  gohi(MDATA);
  gohi(MCLK);
  delayMicroseconds(300);
  golo(MCLK);
  delayMicroseconds(300);
  golo(MDATA);
  delayMicroseconds(10);
  
  gohi(MCLK);
 
  while (digitalRead(MCLK) == HIGH)
    ;
  
  for (i=0; i < 8; i++){
    if (data & 0x01){
      gohi(MDATA);
    } 
    else{
      golo(MDATA);
    }
    
    while (digitalRead(MCLK) == LOW);
    while (digitalRead(MCLK) == HIGH);
    parity = parity ^ (data & 0x01);
    data = data >> 1;
  }  
  /* parity */
  if (parity){
    gohi(MDATA);
  }else {
    golo(MDATA);
  }
  while (digitalRead(MCLK) == LOW);
  while (digitalRead(MCLK) == HIGH);
  /* stop bit */
  gohi(MDATA);
  delayMicroseconds(50);
  while (digitalRead(MCLK) == HIGH);
  /* wait for mouse to switch modes */
  while ((digitalRead(MCLK) == LOW) || (digitalRead(MDATA) == LOW));
  /* put a hold on the incoming data. */
  golo(MCLK);
  
}


char mouse_read(void){
  char data = 0x00;
  int i;
  char bit = 0x01;

  /* start the clock */
  gohi(MCLK);
  gohi(MDATA);
  delayMicroseconds(50);
  while (digitalRead(MCLK) == HIGH);
  delayMicroseconds(5);  /* not sure why */
  while (digitalRead(MCLK) == LOW); /* eat start bit */
    
  for (i=0; i < 8; i++){
    while (digitalRead(MCLK) == HIGH);
    if (digitalRead(MDATA) == HIGH){
      data = data | bit;
    }
    while (digitalRead(MCLK) == LOW);
    bit = bit << 1;
  }
  /* eat parity bit, which we ignore */
  while (digitalRead(MCLK) == HIGH);
  while (digitalRead(MCLK) == LOW);
  /* eat stop bit */
  while (digitalRead(MCLK) == HIGH);
  while (digitalRead(MCLK) == LOW);
  /* put a hold on the incoming data. */
  golo(MCLK);
   
  return data;
}

void mouse_init()
{ char mouseId;
   
  gohi(MCLK);
  gohi(MDATA);
  
  mouse_write(0xff);
  mouse_read();  /* ack byte */
  mouse_read();  /* blank */
  mouse_read();  /* blank */
  mouse_write(0xf3);  /* Set rate command */
  mouse_read();  /* ack */
  mouse_write(0xC8);  /* Set rate command */
  mouse_read();  /* ack */
  mouse_write(0xf3);  /* Set rate command */
  mouse_read();  /* ack */
  mouse_write(0x64);  /* Set rate command */
  mouse_read();  /* ack */
  mouse_write(0xf3);  /* Set rate command */
  mouse_read();  /* ack */
  mouse_write(0x50);  /* Set rate command */
  mouse_read();  /* ack */
  mouse_write(0xf2);  /* Set rate command */
  mouse_read();  /* ack */
  mouse_read();  /* mouse id, if this value is 0x00 mouse is standard, if it is 0x03 mouse is Intellimouse */
  mouse_write(0xe8);  /* Set wheel resolution */
  mouse_read();  /* ack */
  mouse_write(0x03);  /* 8 counts per mm */
  mouse_read();  /* ack */
  mouse_write(0xe6);  /* scaling 1:1 */
  mouse_read();  /* ack */
  mouse_write(0xf3);  /* Set sample rate */
  mouse_read();  /* ack */
  mouse_write(0x28);  /* Set sample rate */
  mouse_read();  /* ack */
  mouse_write(0xf4);  /* Enable device */
  mouse_read();  /* ack */

  mouse_write(0xf0);  /* remote mode */
  mouse_read();  /* ack */
 
  delayMicroseconds(100);
}

int incomingByte = 0;    // for incoming serial data
char byteRead[30];
bool lmb = false;
bool mmb = false;
bool rmb = false;


void setup(){
  Serial.begin(9600);
  Serial.setTimeout(10);
  mouse_init();
  pinMode(13, OUTPUT);
  
}

/*
 * get a reading from the mouse and report it back to the
 * host via the serial line.
 */
 int my_atoi(char *p){
    int k = 0;
    while (*p){
        k = (k<<3)+(k<<1)+(*p)-'0';
        p++;
    }
    return k;
}
void loop(){
  int numBytes = 0;

  //Serial.println("Please enter a character (100ms timeout)");

  //while (!Serial.available()){
  if (Serial.available()> 0) {
    //if (Serial.available() > 0) {
    //byteRead[0] = (char)0;
    memset(byteRead, 0, sizeof(byteRead));
    numBytes = Serial.readBytes(byteRead, 30);
    if ((String)byteRead[numBytes-1]=="\n" && numBytes <= 6 && numBytes >= 2 ){
      Serial.print(numBytes);Serial.print("|"); Serial.print((String)byteRead);
      if((String)byteRead =="13on\n"){
        digitalWrite(13, HIGH); 
      }
      if((String)byteRead =="13off\n"){
        digitalWrite(13, LOW); 
      }
      Serial.flush(); //wait for serial buffer to empty before letting mouse to print to serial
    } else {
        Serial.print(numBytes);Serial.print("|"); Serial.println("Bad data!");
        Serial.flush();
    }

    //}
 
  }

  


  //get a reading from the mouse 
  mouse_write(0xeb);// give me data! 
  mouse_read();// ignore ack */

  char btn = mouse_read();
  char move_x = mouse_read();
  char move_y = mouse_read();
  char move_z = mouse_read();

  int x_y_response_time = 5;// adjust between 0 (fast) and 100 (slow)

  //adjust the code below to control your LED's, motors etc
  if(bitRead(btn, 0)){               //left button pressed
    if(!lmb){
    Serial.println("btnLeft");
    lmb = true;      
    }       
  }
  if(!bitRead(btn, 0)){              //left button released
    if(lmb){
      Serial.println("!btnLeft");
      lmb = false;
    }
  }
    if(bitRead(btn, 1)){             //right button pressed
    if(!rmb){
    Serial.println("btnRight");
    rmb = true;      
    }       
  }
  if(!bitRead(btn, 1)){              //right button released
    if(rmb){
      Serial.println("!btnRight");
      rmb = false;
    }
  }
    if(bitRead(btn, 2)){             //middle button pressed
    if(!mmb){
    Serial.println("btnMiddle");
    mmb = true;      
    }       
  }
  if(!bitRead(btn, 2)){              //middle button released
    if(mmb){
      Serial.println("!btnMiddle");
      mmb = false;
    }
  }

  if(move_x > x_y_response_time){   //mouse move right
    Serial.println("right");
  }
  if(move_x < -x_y_response_time){  //mouse move left
    Serial.println("left");
  }
  if(move_y > x_y_response_time){   //mouse move up
    Serial.println("up");
  }
  if(move_y < -x_y_response_time){  //mouse move down
    Serial.println("down");
  }
  if(move_z > 0){                   //scroll down
    Serial.println("scrollDown");
  }
  if(move_z < 0){                   //scroll up
    Serial.println("scrollUp");
  }

  delay(20);
}
