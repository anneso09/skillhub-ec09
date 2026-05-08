<?php

namespace Tests\Feature;

use Tests\TestCase;
use App\Models\Formation;
use App\Models\Enrollment;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Http;

class ApprenantsFormationTest extends TestCase
{
    use RefreshDatabase;

    private function mockAuth(int $userId, string $role = 'formateur'): array
    {
        Http::fake([
            '*/api/auth/validate' => Http::response([
                'email'  => 'formateur@test.com',
                'role'   => $role,
                'userId' => $userId,
            ], 200),
        ]);

        return ['Authorization' => 'Bearer fake_token'];
    }

    private function createFormateur(): User
    {
        return User::create([
            'nom'      => 'Formateur',
            'prenom'   => 'Test',
            'email'    => 'formateur@test.com',
            'password' => 'password',
            'role'     => 'formateur',
        ]);
    }

    private function createApprenant(): User
    {
        return User::create([
            'nom'      => 'Apprenant',
            'prenom'   => 'Test',
            'email'    => 'apprenant@test.com',
            'password' => 'password',
            'role'     => 'apprenant',
        ]);
    }

    private function createFormation(int $formateurId): Formation
    {
        return Formation::create([
            'titre'        => 'Formation Test',
            'description'  => 'Description test',
            'categorie'    => 'Test',
            'niveau'       => 'Débutant',
            'formateur_id' => $formateurId,
        ]);
    }

    /** @test */
    public function formateur_proprio_voit_liste_apprenants()
    {
        $formateur  = $this->createFormateur();
        $apprenant  = $this->createApprenant();
        $formation  = $this->createFormation($formateur->id);
        $headers    = $this->mockAuth($formateur->id);

        Enrollment::create([
            'utilisateur_id' => $apprenant->id,
            'formation_id'   => $formation->id,
            'progression'    => 50,
        ]);

        $response = $this->getJson(
            "/api/formations/{$formation->id}/apprenants",
            $headers
        );

        $response->assertStatus(200);
        $response->assertJsonFragment([
            'id'    => $apprenant->id,
            'nom'   => 'Apprenant',
            'email' => 'apprenant@test.com',
        ]);
    }

    /** @test */
    public function formateur_proprio_voit_tableau_vide_si_aucun_apprenant()
    {
        $formateur = $this->createFormateur();
        $formation = $this->createFormation($formateur->id);
        $headers   = $this->mockAuth($formateur->id);

        $response = $this->getJson(
            "/api/formations/{$formation->id}/apprenants",
            $headers
        );

        $response->assertStatus(200);
        $response->assertJson([]);
    }

    /** @test */
    public function formateur_non_proprio_recoit_403()
    {
        $formateur1 = $this->createFormateur();
        $formateur2 = User::create([
            'nom'      => 'Formateur2',
            'prenom'   => 'Test',
            'email'    => 'formateur2@test.com',
            'password' => 'password',
            'role'     => 'formateur',
        ]);

        $formation = $this->createFormation($formateur1->id);
        $headers   = $this->mockAuth($formateur2->id);

        $response = $this->getJson(
            "/api/formations/{$formation->id}/apprenants",
            $headers
        );

        $response->assertStatus(403);
    }

    /** @test */
    public function formation_inexistante_retourne_404()
    {
        $formateur = $this->createFormateur();
        $headers   = $this->mockAuth($formateur->id);

        $response = $this->getJson(
            "/api/formations/999/apprenants",
            $headers
        );

        $response->assertStatus(404);
    }
}