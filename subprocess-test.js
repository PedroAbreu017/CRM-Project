const { spawnSync } = require('child_process');

// Teste com shell
const shellResult = spawnSync('node', ['--version'], { shell: true });
console.log('Comando com shell:');
console.log('Status:', shellResult.status);
console.log('Saída:', shellResult.stdout?.toString() || 'Sem saída');
console.log('Erro:', shellResult.stderr?.toString() || 'Sem erro');

// Teste sem shell
const noShellResult = spawnSync('node', ['--version'], { shell: false });
console.log('\nComando sem shell:');
console.log('Status:', noShellResult.status);
console.log('Saída:', noShellResult.stdout?.toString() || 'Sem saída');
console.log('Erro:', noShellResult.stderr?.toString() || 'Sem erro');

// Teste com caminho completo (ajuste o caminho conforme necessário)
const fullPathResult = spawnSync('C:\\Program Files\\nodejs\\node.exe', ['--version']);
console.log('\nComando com caminho completo:');
console.log('Status:', fullPathResult.status);
console.log('Saída:', fullPathResult.stdout?.toString() || 'Sem saída');
console.log('Erro:', fullPathResult.stderr?.toString() || 'Sem erro');

// Imprime variáveis de ambiente relevantes
console.log('\nVariáveis de ambiente:');
console.log('PATH:', process.env.PATH);
console.log('PATHEXT:', process.env.PATHEXT);