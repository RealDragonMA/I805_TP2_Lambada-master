DATA SEGMENT
	prixTtc DD
	prixHt DD
DATA ENDS
CODE SEGMENT
	mov eax, 200
	push eax
	mov prixHt, eax
	pop eax
	pop ebx
	imul eax, ebx
	push eax
	pop eax
	pop ebx
	idiv eax, ebx
	push eax
	mov prixTtc, eax
CODE ENDS
