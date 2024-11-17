<div *ngFor="let field of fields; let i = index">
    <span>{{ field }}</span>
    <hr *ngIf="field.label === 'Doc Name'">
    <span>{{ values[i] }}</span>
  </div>
