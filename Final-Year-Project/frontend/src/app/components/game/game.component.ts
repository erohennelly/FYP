import {
  Component,
  OnInit,
  HostListener,
  ElementRef,
  ViewChild,
} from '@angular/core';

import { Player } from './../../models/player';
import { Movement } from '../../models/movement';
import { AppComponent } from '../../app.component';
import { GameService } from '../../services/game.service';
import { ServerUpdateModel } from 'src/app/models/serverUpdate';
import { ServerMessage } from 'src/app/models/serverMessage';
import { AttackModel } from 'src/app/models/attckModel';
import { BasicGameComponentModel } from 'src/app/models/basicGameComponentModel';

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})


export class GameComponent implements OnInit {
  constructor(private game: GameService, private appComponent: AppComponent) {}

  @ViewChild('myCanvas') myCanvas: ElementRef;
  public context: CanvasRenderingContext2D;
  private width = 1500;
  private height = 700;
  private mouseXLocation = 0;
  private mouseYLocation = 0;
  private player: Player;
  private playerArray: Array<Player> = [];
  private flowerArray: Array<BasicGameComponentModel> = [];
  private beesArray: Array<BasicGameComponentModel> = [];
  private score = 0;
  private userName: string = this.appComponent.username || 'user' + Math.floor((Math.random() * 1000) + 1);

  private flowersPositions = [
    [0, 40],
    [40, 0],
    [-40, 0],
    [0, -40],
    [-27, -27],
    [27, 27],
    [-27, 27],
    [27, -27],
  ];

  ngOnInit() {
    this.context = (this.myCanvas.nativeElement as HTMLCanvasElement).getContext('2d');
    setInterval(this.calculateMovement, 200);

    this.game.gameUpdates.subscribe(msg => {
      console.log({ msg })
      const data: ServerUpdateModel = msg;
      this.playerArray = data.players;
      this.flowerArray = data.flowers;
      this.beesArray = data.bees;
      this.draw();
    });

    this.game.playerJoins.subscribe(msg => {
      this.player = msg;
    });

    this.game.sendPlayerJoin(this.userName);
  }

  ngOnDestroy(): void {
    if (this.player) {
      this.game.sendPlayerLeave(this.player.id);
    }
  }
  calculateMovement() {
    if (this.player) {
      const serverMessage = new ServerMessage();
      const movement = new Movement();
      const angle = Math.atan2((this.player.ypos - this.mouseYLocation), (this.player.xpos - this.mouseXLocation));
      const xPos = 10 * Math.cos(angle);
      const yPos = 10 * Math.sin(angle);

      serverMessage.movement = movement;
      movement.id = this.player.id;
      movement.xMovement = -xPos;
      movement.yMovement = -yPos;

      console.log({ serverMessage });
      this.game.sendGameUpdates(serverMessage);
    } else {
      // console.log(this.player);
    }
  }

  draw() {
    const ctx = this.context;
    const HIT_COLOR = '#06080c';
    ctx.fillStyle = HIT_COLOR;
    ctx.fillRect(0, 0, this.width, this.height);

    this.flowerArray.map(flower => {
      this.flowersPositions.forEach(flowerPos => {
        ctx.fillStyle = '#ffffff';
        ctx.beginPath();
        ctx.arc(flower.xpos + flowerPos[0], flower.ypos + flowerPos[1], 18, 0, 2 * Math.PI, true);
        ctx.fill();
      });

      ctx.fillStyle = '#FFFF00';
      ctx.beginPath();
      ctx.arc(flower.xpos, flower.ypos, 30, 0, 2 * Math.PI, true);
      ctx.fill();
    });

    this.playerArray.map(player => {
      if (player.userName === this.userName) {
        this.player = player;
      }
      const colors =  player.color.values();
      ctx.fillStyle = colors.next().value;

      const oneThirdLength = player.length / 3;

      ctx.fillRect(player.xpos, player.ypos, player.length, oneThirdLength);
      ctx.fillRect(player.xpos, player.ypos + oneThirdLength * 2, player.length, oneThirdLength);
      ctx.fillStyle = colors.next().value;
      ctx.fillRect(player.xpos, player.ypos + oneThirdLength, player.length, oneThirdLength);
    });

    this.beesArray.map(bee => {
      const radius = bee.length;
      const colors =  bee.color.values();

      ctx.fillStyle = colors.next().value;
      ctx.beginPath();
      ctx.arc(bee.xpos, bee.ypos, radius, 0, Math.PI, true);
      ctx.fill();

      ctx.fillStyle = colors.next().value;
      ctx.beginPath();
      ctx.arc(bee.xpos, bee.ypos, radius, Math.PI, 2 * Math.PI, true);
      ctx.fill();
    });
  }

  @HostListener('click', ['$event'])
  onClick(btn: MouseEvent) {
    const xLocation = btn.clientX - this.context.canvas.getBoundingClientRect().left;
    const yLocation = btn.clientY - this.context.canvas.getBoundingClientRect().top;
    this.playerArray
      .filter(player => player.id !== this.player.id)
      .map(player => {
        if (Math.abs(player.xpos - xLocation) < player.length &&
          Math.abs(player.ypos - yLocation) < player.length) {
            const attackModel = new AttackModel();
            const serverMessage = new ServerMessage();

            attackModel.sender = this.player;
            attackModel.target = player;
            serverMessage.attack = attackModel;

            this.game.sendGameUpdates(serverMessage);
          }
      });
 }

 @HostListener('mousemove', ['$event'])
  oninput(input: MouseEvent) {
    this.mouseXLocation = input.clientX - this.context.canvas.getBoundingClientRect().left;
    this.mouseYLocation = input.clientY - this.context.canvas.getBoundingClientRect().top;
    const serverMessage = new ServerMessage();
    const movement = new Movement();
    const angle = Math.atan2((this.player.ypos - this.mouseYLocation), (this.player.xpos - this.mouseXLocation));
    const xPos = 2 * Math.cos(angle);
    const yPos = 2 * Math.sin(angle);

    serverMessage.movement = movement;
    movement.id = this.player.id;
    movement.xMovement = -xPos;
    movement.yMovement = -yPos;

    this.game.sendGameUpdates(serverMessage);
  }
}
